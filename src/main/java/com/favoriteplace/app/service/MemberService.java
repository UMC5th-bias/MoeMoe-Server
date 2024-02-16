package com.favoriteplace.app.service;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.item.Item;
import com.favoriteplace.app.dto.UserInfoResponseDto;
import com.favoriteplace.app.dto.member.MemberDto;
import com.favoriteplace.app.dto.member.MemberDto.EmailDuplicateResDto;
import com.favoriteplace.app.dto.member.MemberDto.EmailSendReqDto;
import com.favoriteplace.app.dto.member.MemberDto.MemberDetailResDto;
import com.favoriteplace.app.dto.member.MemberDto.MemberSignUpReqDto;
import com.favoriteplace.app.repository.ItemRepository;
import com.favoriteplace.app.repository.MemberRepository;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.gcpImage.UploadImage;
import com.favoriteplace.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.favoriteplace.global.exception.ErrorCode.USER_ALREADY_EXISTS;
import static com.favoriteplace.global.exception.ErrorCode.USER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    public final SecurityUtil securityUtil;
    private final UploadImage uploadImage;
    private final ItemRepository itemRepository;

    @Transactional
    public MemberDto.MemberDetailResDto signup(MemberSignUpReqDto memberSignUpReqDto, List<MultipartFile> images)
        throws IOException {
        memberRepository.findByEmail(memberSignUpReqDto.getEmail())
            .ifPresentOrElse(
                existingMember -> {
                    throw new RestApiException(USER_ALREADY_EXISTS);
                },
                () -> {
                    // 값이 없을 때 수행할 동작 (예외를 발생시키지 않는 경우)
                }
            );

        String uuid = null;
        String password = passwordEncoder.encode(memberSignUpReqDto.getPassword());

        if (images != null && !images.get(0).isEmpty()) {
            uuid = uploadImage.uploadImageToCloud(images.get(0));
        }

        Item titleItem = itemRepository.findByName("새싹회원").get();

        Member member = memberSignUpReqDto.toEntity(password, uuid, titleItem);
        memberRepository.save(member);

        return MemberDetailResDto.from(member);
    }

    @Transactional
    public MemberDto.EmailDuplicateResDto emailDuplicateCheck(EmailSendReqDto emailSendReqDto) {
        String email = emailSendReqDto.getEmail();
        Boolean isExists = memberRepository.findByEmail(email).isPresent();

        return new EmailDuplicateResDto(isExists);
    }

    @Transactional
    public void setNewPassword(String email, String password) {
        Member member = memberRepository.findByEmail(email)
            .orElseThrow(() -> new RestApiException(USER_NOT_FOUND));

        String newPassword = passwordEncoder.encode(password);
        member.updatePassword(newPassword);
    }

    @Transactional
    public UserInfoResponseDto getUserInfo(Member member) {
        if(member != null){
            return UserInfoResponseDto.of(member);
        }
        return null;
    }
}