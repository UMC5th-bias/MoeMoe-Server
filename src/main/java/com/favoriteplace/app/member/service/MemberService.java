package com.favoriteplace.app.member.service;

import static com.favoriteplace.global.exception.ErrorCode.USER_ALREADY_EXISTS;
import static com.favoriteplace.global.exception.ErrorCode.USER_NOT_FOUND;

import com.favoriteplace.app.member.controller.dto.MemberSignUpReqDto;
import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.item.domain.Item;
import com.favoriteplace.app.member.controller.dto.UserInfoResponseDto;
import com.favoriteplace.app.member.controller.dto.KaKaoSignUpRequestDto;
import com.favoriteplace.app.member.controller.dto.MemberDto;
import com.favoriteplace.app.member.controller.dto.MemberDto.EmailDuplicateResDto;
import com.favoriteplace.app.member.controller.dto.MemberDto.EmailSendReqDto;
import com.favoriteplace.app.item.repository.ItemRepository;
import com.favoriteplace.app.member.domain.enums.LoginType;
import com.favoriteplace.app.member.repository.MemberRepository;
import com.favoriteplace.global.auth.kakao.KakaoClient;
import com.favoriteplace.global.auth.provider.JwtTokenProvider;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.s3Image.AmazonS3ImageManager;

import java.io.IOException;
import java.util.List;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final AmazonS3ImageManager amazonS3ImageManager;
    private final ItemRepository itemRepository;

    public void kakaoLogin(final String email) {
        Member member = findMember(email);
    }

    @Transactional
    public Member kakaoSignUp(
            final String email,
            final KaKaoSignUpRequestDto memberSignUpReqDto,
            final List<MultipartFile> images
    ) throws IOException {
        memberRepository.findByEmail(email)
                .ifPresent(a -> {
                    throw new RestApiException(USER_ALREADY_EXISTS);
                });
        String profileImage = getProfileImageFromRequest(images);
        Item titleItem = getDefaultProfileItem();
        Member member = saveUser(
                memberSignUpReqDto.nickname(),
                email,
                memberSignUpReqDto.snsAllow(),
                memberSignUpReqDto.introduction(),
                profileImage,
                titleItem,
                LoginType.KAKAO
        );
        return member;
    }

    @Transactional
    public Member signup(
            final MemberSignUpReqDto memberSignUpReqDto,
            final List<MultipartFile> images
    ) throws IOException {
        String password = passwordEncoder.encode(memberSignUpReqDto.password());
        Item titleItem = getDefaultProfileItem();
        String profileImage = getProfileImageFromRequest(images);
        Member member = saveUser(memberSignUpReqDto.nickname(), memberSignUpReqDto.email(),
                memberSignUpReqDto.snsAllow(), memberSignUpReqDto.introduction(),
                profileImage, titleItem, LoginType.SELF);
        member.updatePassword(password);
        return member;
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

    public Member findMember(final String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new RestApiException(USER_NOT_FOUND));
    }

    private Item getDefaultProfileItem() {
        return itemRepository.findByName("새싹회원").get();
    }

    private Member saveUser(
            String nickname, String email,
            boolean snsAllow, String introduction,
            String profileImage, Item titleItem, LoginType loginType) {
        Member member =
                Member.create(
                        nickname, email,
                        snsAllow, introduction,
                        profileImage, titleItem, loginType
                );
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