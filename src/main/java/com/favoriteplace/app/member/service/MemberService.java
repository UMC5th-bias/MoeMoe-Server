package com.favoriteplace.app.member.service;

import static com.favoriteplace.global.exception.ErrorCode.NOT_SIGNUP_WITH_KAKAO;
import static com.favoriteplace.global.exception.ErrorCode.USER_ALREADY_EXISTS;
import static com.favoriteplace.global.exception.ErrorCode.USER_NOT_FOUND;

import com.favoriteplace.app.item.domain.Item;
import com.favoriteplace.app.item.repository.ItemRepository;
import com.favoriteplace.app.member.controller.dto.AuthKakaoLoginDto;
import com.favoriteplace.app.member.controller.dto.KaKaoSignUpRequestDto;
import com.favoriteplace.app.member.controller.dto.MemberDto;
import com.favoriteplace.app.member.controller.dto.TokenInfoDto;
import com.favoriteplace.app.member.controller.dto.UserInfoResponseDto;
import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.member.repository.MemberRepository;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.s3Image.AmazonS3ImageManager;
import com.favoriteplace.global.auth.kakao.KakaoClient;
import com.favoriteplace.global.auth.provider.JwtTokenProvider;
import com.favoriteplace.global.util.SecurityUtil;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityUtil securityUtil;
    private final AmazonS3ImageManager amazonS3ImageManager;
    private final ItemRepository itemRepository;
    private final RedisTemplate redisTemplate;
    private final KakaoClient kakaoClient;

    public TokenInfoDto kakaoLogin(final String token) {
        AuthKakaoLoginDto userInfo = kakaoClient.getUserInfo(token);

        // 최초 로그인이라면 회원가입 API로 통신하도록
        Member member = memberRepository.findByEmail(userInfo.kakaoAccount().email())
                .orElseThrow(() -> new RestApiException(NOT_SIGNUP_WITH_KAKAO));

        return jwtTokenProvider.generateToken(userInfo.kakaoAccount().email());
    }

    @Transactional
    public MemberDto.MemberSignUpResDto kakaoSignUp(
            final String token,
            final KaKaoSignUpRequestDto memberSignUpReqDto,
            final List<MultipartFile> images
    ) throws IOException {

        String userEmail = kakaoClient.getUserInfo(token).kakaoAccount().email();

        memberRepository.findByEmail(userEmail)
                .ifPresent(a -> {
                    throw new RestApiException(USER_ALREADY_EXISTS);
                });

        String profileImageUrl = null;
        if (images != null && !images.get(0).isEmpty()) {
            profileImageUrl = uploadProfileImage(images.get(0));
        }

        Item titleItem = itemRepository.findByName("새싹회원").get();

        Member member = memberSignUpReqDto.toEntity(profileImageUrl, titleItem, userEmail);
        memberRepository.save(member);

        TokenInfoDto tokenInfo = jwtTokenProvider.generateToken(userEmail);
        member.updateRefreshToken(tokenInfo.refreshToken());

        return MemberDto.MemberSignUpResDto.from(member, tokenInfo);

    }

    @Transactional
    public MemberDto.MemberSignUpResDto signup(
            final MemberDto.MemberSignUpReqDto memberSignUpReqDto,
            final List<MultipartFile> images
    ) throws IOException {

        memberRepository.findByEmail(memberSignUpReqDto.getEmail())
                .ifPresent(
                        existingMember -> {
                            throw new RestApiException(USER_ALREADY_EXISTS);
                        }
                );

        String profileImageUrl = null;
        if (images != null && !images.get(0).isEmpty()) {
            profileImageUrl = uploadProfileImage(images.get(0));
        }

        String password = passwordEncoder.encode(memberSignUpReqDto.getPassword());

        Item titleItem = itemRepository.findByName("새싹회원").get();

        Member member = memberSignUpReqDto.toEntity(password, profileImageUrl, titleItem);
        memberRepository.save(member);

        TokenInfoDto tokenInfo = jwtTokenProvider.generateToken(member.getEmail());
        member.updateRefreshToken(tokenInfo.refreshToken());

        return MemberDto.MemberSignUpResDto.from(member, tokenInfo);
    }

    public String uploadProfileImage(MultipartFile profileImage) throws IOException {
        return amazonS3ImageManager.upload(profileImage).join();
    }

    @Transactional
    public MemberDto.EmailDuplicateResDto emailDuplicateCheck(MemberDto.EmailSendReqDto emailSendReqDto) {
        String email = emailSendReqDto.getEmail();
        Boolean isExists = memberRepository.findByEmail(email).isPresent();

        return new MemberDto.EmailDuplicateResDto(isExists);
    }

    @Transactional
    public void setNewPassword(String email, String password) {
        Member member = findMember(email);

        String newPassword = passwordEncoder.encode(password);
        member.updatePassword(newPassword);
    }

    @Transactional
    public UserInfoResponseDto getUserInfo(Member member) {
        if (member != null) {
            return UserInfoResponseDto.of(member);
        }
        return null;
    }

    @Transactional
    public void logout(String token){
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        Member member = findMember(authentication.getName());

        if (member.getRefreshToken() != null && !member.getRefreshToken().isEmpty()) {
            member.deleteRefreshToken(member.getRefreshToken());
        }

        /* 해당 asscess token 유효시간을 계산해서 blacklist로 저장 */
        Long expriation = jwtTokenProvider.getExpiration(token);
        log.info(String.valueOf(expriation));
        redisTemplate.opsForValue()
                .set(token, "logout", expriation, TimeUnit.MILLISECONDS);

    }

    public Member findMember(final String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new RestApiException(USER_NOT_FOUND));
    }

}