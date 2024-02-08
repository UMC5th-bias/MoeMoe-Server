package com.favoriteplace.app.service;

import static com.favoriteplace.global.exception.ErrorCode.USER_ALREADY_EXISTS;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.domain.item.Item;
import com.favoriteplace.app.dto.UserInfoResponseDto;
import com.favoriteplace.app.dto.member.MemberDto;
import com.favoriteplace.app.dto.member.MemberDto.EmailCheckReqDto;
import com.favoriteplace.app.dto.member.MemberDto.EmailDuplicateResDto;
import com.favoriteplace.app.dto.member.MemberDto.EmailSendReqDto;
import com.favoriteplace.app.dto.member.MemberDto.MemberDetailResDto;
import com.favoriteplace.app.dto.member.MemberDto.MemberSignUpReqDto;
import com.favoriteplace.app.repository.GuestBookRepository;
import com.favoriteplace.app.repository.ItemRepository;
import com.favoriteplace.app.repository.MemberRepository;
import com.favoriteplace.app.repository.PostRepository;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.gcpImage.UploadImage;
import com.favoriteplace.global.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final GuestBookRepository guestBookRepository;
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
            System.out.println("hi");
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
    public UserInfoResponseDto getUserInfo(HttpServletRequest request) {
        Member member = securityUtil.getUserFromHeader(request);
        if(member != null){
            return UserInfoResponseDto.of(member);
        }
        return null;
    }

    @Transactional
    public UserInfoResponseDto getUserInfoByPostId(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if(optionalPost.isEmpty()){
            throw new RestApiException(ErrorCode.POST_NOT_FOUND);
        }
        return UserInfoResponseDto.of(optionalPost.get().getMember());
    }

    @Transactional
    public UserInfoResponseDto getUserInfoByGuestBookId(Long guestBookId){
        Optional<GuestBook> optionalGuestBook = guestBookRepository.findById(guestBookId);
        if(optionalGuestBook.isEmpty()){
            throw new RestApiException(ErrorCode.GUESTBOOK_NOT_FOUND);
        }
        return UserInfoResponseDto.of(optionalGuestBook.get().getMember());
    }
}