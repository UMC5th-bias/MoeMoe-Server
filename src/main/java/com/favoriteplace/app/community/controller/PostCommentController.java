package com.favoriteplace.app.community.controller;

import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.community.controller.dto.comment.CommentCreateRequestDto;
import com.favoriteplace.app.community.controller.dto.comment.CommentModifyRequestDto;
import com.favoriteplace.app.community.controller.dto.comment.CommentRootResponseDto;
import com.favoriteplace.app.community.service.CommentCommandService;
import com.favoriteplace.app.community.service.CommentQueryService;
import com.favoriteplace.global.util.SecurityUtil;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.favoriteplace.app.community.controller.dto.PostResponseDto.MyCommentDto;
import static com.favoriteplace.app.community.controller.dto.PostResponseDto.CommentSuccessResponseDto;

@RestController
@RequestMapping("/posts/free")
@RequiredArgsConstructor
public class PostCommentController {

    private final SecurityUtil securityUtil;
    private final CommentQueryService commentQueryService;
    private final CommentCommandService commentCommandService;

    @GetMapping("/my-comments")
    public ResponseEntity<MyCommentDto> getMyComments(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ) {
        Member member = securityUtil.getUser();
        return ResponseEntity.ok(commentQueryService.getMyPostComments(member, page, size));
    }

    @GetMapping("/{post_id}/comments")
    public ResponseEntity<CommentRootResponseDto> getPostComments(
            @PathVariable("post_id") Long postId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "5") int size,
            HttpServletRequest request
    ) {
        Member member = securityUtil.getUserFromHeader(request);
        return ResponseEntity.ok(commentQueryService.getPostComments(member, page, size, postId));
    }

    @PostMapping("/{post_id}/comments")
    public ResponseEntity<CommentSuccessResponseDto> createPostComment(
            @PathVariable("post_id") Long postId,
            @RequestBody CommentCreateRequestDto dto
    ) {
        Member member = securityUtil.getUser();
        Long commentId = commentCommandService.createPostComment(member, postId, dto);
        return new ResponseEntity<>(
                CommentSuccessResponseDto.builder().commentId(commentId).build(),
                HttpStatus.OK
        );
    }

    @PostMapping("/{post_id}/comments/{comment_id}/notification")
    public ResponseEntity<Void> sendPostNotification(
            @PathVariable("post_id") long postId,
            @PathVariable("comment_id") long commentId
    ) {
        commentCommandService.sendPostNotification(postId, commentId);
        return ResponseEntity.ok().build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "204")
    })
    @PutMapping("/comments/{comment_id}")
    public ResponseEntity<Void> modifyPostComment(
            @PathVariable("comment_id") long commentId,
            @RequestBody CommentModifyRequestDto dto
    ) {
        Member member = securityUtil.getUser();
        commentCommandService.modifyComment(member, commentId, dto.content());
        return new ResponseEntity<>(
                HttpStatus.NO_CONTENT
        );
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "204")
    })
    @DeleteMapping("/comments/{comment_id}")
    public ResponseEntity<CommentSuccessResponseDto> deletePostComment(
            @PathVariable("comment_id") long commentId
    ) {
        Member member = securityUtil.getUser();
        commentCommandService.deleteComment(member, commentId);
        return new ResponseEntity<>(
                HttpStatus.NO_CONTENT
        );
    }
}
