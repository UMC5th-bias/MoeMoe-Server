package com.favoriteplace.app.member.service;

import static com.favoriteplace.global.exception.ErrorCode.TOKEN_NOT_VALID;
import static com.favoriteplace.global.exception.ErrorCode.USER_ALREADY_EXISTS;
import static com.favoriteplace.global.exception.ErrorCode.USER_NOT_FOUND;

import com.favoriteplace.app.member.controller.dto.MemberSignUpReqDto;
import com.favoriteplace.app.member.controller.dto.TokenInfoDto;
import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.item.domain.Item;
import com.favoriteplace.app.member.controller.dto.UserInfoResponseDto;
import com.favoriteplace.app.member.controller.dto.KaKaoSignUpRequestDto;
import com.favoriteplace.app.member.controller.dto.MemberDto;
import com.favoriteplace.app.member.controller.dto.MemberDto.EmailDuplicateResDto;
import com.favoriteplace.app.member.controller.dto.MemberDto.EmailSendReqDto;
import com.favoriteplace.app.item.repository.ItemRepository;
import com.favoriteplace.app.member.repository.MemberRepository;
import com.favoriteplace.global.auth.kakao.KakaoClient;
import com.favoriteplace.global.auth.provider.JwtTokenProvider;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.s3Image.AmazonS3ImageManager;

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
    private final AmazonS3ImageManager amazonS3ImageManager;
    private final ItemRepository itemRepository;
    private final RedisTemplate redisTemplate;
    private final KakaoClient kakaoClient;

    public TokenInfoDto kakaoLogin(final String token) {
        String userEmail = getUserEmailFromKakao(token);

        // 최초 로그인이라면 회원가입 API로 통신하도록
        Member member = findMember(userEmail);

        return issueToken(userEmail);
    }

    @Transactional
    public MemberDto.MemberSignUpResDto kakaoSignUp(
            final String token,
            final KaKaoSignUpRequestDto memberSignUpReqDto,
            final List<MultipartFile> images
    ) throws IOException {

        String userEmail = getUserEmailFromKakao(token);

        memberRepository.findByEmail(userEmail)
                .ifPresent(a -> {
                    throw new RestApiException(USER_ALREADY_EXISTS);
                });

        String profileImage = getProfileImageFromRequest(images);

        Item titleItem = getDefaultProfileItem();

        Member member = saveUser(memberSignUpReqDto.nickname(), userEmail,
                memberSignUpReqDto.snsAllow(), memberSignUpReqDto.introduction(),
                profileImage, titleItem);

        TokenInfoDto tokenInfo = issueToken(userEmail);
        member.updateRefreshToken(tokenInfo.refreshToken());

        return MemberDto.MemberSignUpResDto.from(member, tokenInfo);

    }

    @Transactional
    public MemberDto.MemberSignUpResDto signup(
            final MemberSignUpReqDto memberSignUpReqDto,
            final List<MultipartFile> images
    ) throws IOException {

        String password = passwordEncoder.encode(memberSignUpReqDto.password());

        Item titleItem = getDefaultProfileItem();

        String profileImage = getProfileImageFromRequest(images);

        Member member = saveUser(memberSignUpReqDto.nickname(), memberSignUpReqDto.email(),
                memberSignUpReqDto.snsAllow(), memberSignUpReqDto.introduction(),
                profileImage, titleItem);

        TokenInfoDto tokenInfo= issueToken(member.getEmail());
        member.updateRefreshToken(tokenInfo.refreshToken());

        return MemberDto.MemberSignUpResDto.from(member, tokenInfo);
    }

    public String uploadProfileImage(MultipartFile profileImage) throws IOException {
        return amazonS3ImageManager.upload(profileImage).join();
    }

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

    private Member findMember(final String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new RestApiException(USER_NOT_FOUND));
    }

    private String getUserEmailFromKakao(String token) {
        return kakaoClient.getUserInfo(token).kakaoAccount().email();
    }

    private Item getDefaultProfileItem() {
        return itemRepository.findByName("새싹회원").get();
    }

    private TokenInfoDto issueToken(String email) {
        return jwtTokenProvider.generateToken(email);
    }

    private Member saveUser(
            String nickname, String email,
            boolean snsAllow, String introduction,
            String profileImage, Item titleItem)
    {
        Member member = Member.create(nickname, email, snsAllow, introduction, profileImage, titleItem);
        return memberRepository.save(member);
    }

    private String getProfileImageFromRequest(List<MultipartFile> images) throws IOException {
        String profileImageUrl = null;
        if (images != null && !images.get(0).isEmpty()) {
            profileImageUrl = uploadProfileImage(images.get(0));
        }
        return profileImageUrl;
    }

}