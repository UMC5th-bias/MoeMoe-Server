package com.favoriteplace.app.service;

import static com.favoriteplace.global.exception.ErrorCode.USER_ALREADY_EXISTS;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.dto.UserInfoResponseDto;
import com.favoriteplace.app.dto.member.MemberDto;
import com.favoriteplace.app.dto.member.MemberDto.EmailCheckReqDto;
import com.favoriteplace.app.dto.member.MemberDto.EmailDuplicateResDto;
import com.favoriteplace.app.dto.member.MemberDto.EmailSendReqDto;
import com.favoriteplace.app.dto.member.MemberDto.MemberSignUpReqDto;
import com.favoriteplace.app.repository.MemberRepository;
import com.favoriteplace.app.repository.PostRepository;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;
    public final SecurityUtil securityUtil;

    @Transactional
    public MemberDto.TokenInfo signup(MemberSignUpReqDto memberSignUpReqDto) {
        String password = passwordEncoder.encode(memberSignUpReqDto.getPassword());
        Member member = memberSignUpReqDto.toEntity(password, null);

        //TODO 유저 프로필 이미지 + 새싹회원 칭호 저장
        memberRepository.save(member);
        return null;
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

}