package com.favoriteplace.app.controller;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.dto.community.CommentRequestDto;
import com.favoriteplace.app.dto.community.CommentResponseDto;
import com.favoriteplace.app.dto.community.PostResponseDto;
import com.favoriteplace.app.repository.MemberRepository;
import com.favoriteplace.app.service.community.CommentCommandService;
import com.favoriteplace.app.service.community.CommentQueryService;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts/free")
@RequiredArgsConstructor
public class PostCommentController {

    private final SecurityUtil securityUtil;
    private final CommentQueryService commentQueryService;
    private final CommentCommandService commentCommandService;
    private final MemberRepository memberRepository;

    @GetMapping("/my-comments")
    public PostResponseDto.MyCommentDto getMyComments(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ){
        Member member = securityUtil.getUser();
        List<PostResponseDto.MyComment> comments = commentQueryService.getMyPostComments(member, page, size);
        return PostResponseDto.MyCommentDto.builder()
                .page((long) page)
                .size((long) size)
                .comment(comments)
                .build();
    }

    @GetMapping("/{post_id}/comments")
    public CommentResponseDto.PostCommentDto getPostComments(
            @PathVariable("post_id") Long postId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            HttpServletRequest request
    ){
        Member member = securityUtil.getUserFromHeader(request);
        List<CommentResponseDto.PostComment> comments = commentQueryService.getPostComments(member, page, size, postId);
        return CommentResponseDto.PostCommentDto.builder()
                .page((long)page)
                .size((long)size)
                .comment(comments)
                .build();
    }

    @PostMapping("/{post_id}/comments")
    public ResponseEntity<PostResponseDto.SuccessResponseDto> createPostComment(
            @PathVariable("post_id") long postId,
            @RequestBody CommentRequestDto.CreateComment dto
    ){
        //Member member = securityUtil.getUser();
        Member member = memberRepository.findById(1L).orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));
        commentCommandService.createPostComment(member, postId, dto);
        return new ResponseEntity<>(
                PostResponseDto.SuccessResponseDto.builder().message("댓글을 성공적으로 등록했습니다.").build(),
                HttpStatus.OK
        );
    }

    @PutMapping("/comments/{comment_id}")
    public ResponseEntity<PostResponseDto.SuccessResponseDto> modifyPostComment(
            @PathVariable("comment_id") long commentId,
            @RequestBody CommentRequestDto.ModifyComment dto
    ){
        //Member member = securityUtil.getUser();
        Member member = memberRepository.findById(1L).orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));
        commentCommandService.modifyPostComment(member, commentId, dto.getContent());
        return new ResponseEntity<>(
                PostResponseDto.SuccessResponseDto.builder().message("댓글을 성공적으로 수정했습니다.").build(),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/comments/{comment_id}")
    public ResponseEntity<PostResponseDto.SuccessResponseDto> deletePostComment(
            @PathVariable("comment_id") long commentId
    ){
        //Member member = securityUtil.getUser();
        Member member = memberRepository.findById(1L).orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));
        commentCommandService.deletePostComment(member, commentId);
        return new ResponseEntity<>(
                PostResponseDto.SuccessResponseDto.builder().message("댓글을 성공적으로 삭제했습니다.").build(),
                HttpStatus.OK
        );
    }


}
