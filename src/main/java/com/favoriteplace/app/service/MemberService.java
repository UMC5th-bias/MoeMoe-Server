package com.favoriteplace.app.service;

import static com.favoriteplace.global.exception.ErrorCode.NOT_SIGNUP_WITH_KAKAO;
import static com.favoriteplace.global.exception.ErrorCode.TOKEN_NOT_VALID;
import static com.favoriteplace.global.exception.ErrorCode.USER_ALREADY_EXISTS;
import static com.favoriteplace.global.exception.ErrorCode.USER_NOT_FOUND;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.item.Item;
import com.favoriteplace.app.dto.UserInfoResponseDto;
import com.favoriteplace.app.dto.member.AuthKakaoLoginDto;
import com.favoriteplace.app.dto.member.KaKaoSignUpRequestDto;
import com.favoriteplace.app.dto.member.MemberDto;
import com.favoriteplace.app.dto.member.MemberDto.EmailDuplicateResDto;
import com.favoriteplace.app.dto.member.MemberDto.EmailSendReqDto;
import com.favoriteplace.app.dto.member.MemberDto.MemberSignUpReqDto;
import com.favoriteplace.app.repository.ItemRepository;
import com.favoriteplace.app.repository.MemberRepository;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.s3Image.AmazonS3ImageManager;
import com.favoriteplace.global.auth.kakao.KakaoClient;
import com.favoriteplace.global.auth.provider.JwtTokenProvider;
import com.favoriteplace.global.util.SecurityUtil;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityUtil securityUtil;
    private final AmazonS3ImageManager amazonS3ImageManager;
    private final ItemRepository itemRepository;
    private final RedisTemplate redisTemplate;
    private final KakaoClient kakaoClient;

    public MemberDto.TokenInfo kakaoLogin(final String token) {
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

        MemberDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(userEmail);
        member.updateRefreshToken(tokenInfo.getRefreshToken());

        return MemberDto.MemberSignUpResDto.from(member, tokenInfo);

    }

    @Transactional
    public MemberDto.MemberSignUpResDto signup(
            final MemberSignUpReqDto memberSignUpReqDto,
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

        MemberDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(member.getEmail());
        member.updateRefreshToken(tokenInfo.getRefreshToken());

        return MemberDto.MemberSignUpResDto.from(member, tokenInfo);
    }

    public String uploadProfileImage(MultipartFile profileImage) throws IOException {
        return amazonS3ImageManager.upload(profileImage).join();
    }

    @Transactional
    public MemberDto.EmailDuplicateResDto emailDuplicateCheck(EmailSendReqDto emailSendReqDto) {
        String email = emailSendReqDto.getEmail();
        Boolean isExists = memberRepository.findByEmail(email).isPresent();

        return new EmailDuplicateResDto(isExists);
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
    public void logout(String accessToken) {
        /*1. Access Token 검증 */
        if (!jwtTokenProvider.validateToken(accessToken)) {
            new RestApiException(TOKEN_NOT_VALID);
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        Member member = findMember(authentication.getName());

        if (member.getRefreshToken() != null && !member.getRefreshToken().isEmpty()) {
            member.deleteRefreshToken(member.getRefreshToken());
        }

        /* 해당 asscess token 유효시간을 계산해서 blacklist로 저장 */
        Long expriation = jwtTokenProvider.getExpiration(accessToken);
        redisTemplate.opsForValue()
                .set(accessToken, "logout", expriation, TimeUnit.MICROSECONDS);
    }

    public Member findMember(final String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new RestApiException(USER_NOT_FOUND));
    }

}