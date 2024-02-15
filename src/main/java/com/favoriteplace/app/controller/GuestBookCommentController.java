package com.favoriteplace.app.controller;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.dto.community.CommentResponseDto;
import com.favoriteplace.app.dto.community.GuestBookRequestDto;
import com.favoriteplace.app.dto.community.GuestBookResponseDto;
import com.favoriteplace.app.dto.community.PostResponseDto;
import com.favoriteplace.app.service.community.CommentCommandService;
import com.favoriteplace.app.service.community.CommentQueryService;
import com.favoriteplace.global.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts/guestbooks")
@RequiredArgsConstructor
public class GuestBookCommentController {
    private final SecurityUtil securityUtil;
    private final CommentQueryService commentQueryService;
    private final CommentCommandService commentCommandService;

    @GetMapping("/my-comments")
    public GuestBookResponseDto.MyGuestBookCommentDto getMyComments(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ){
        Member member = securityUtil.getUser();
        List<GuestBookResponseDto.MyGuestBookComment> myComments = commentQueryService.getMyGuestBookComments(member, page, size);
        return GuestBookResponseDto.MyGuestBookCommentDto.builder()
                .page((long) page)
                .size((long) size)
                .comment(myComments)
                .build();
    }

    @GetMapping("/{guestbook_id}/comments")
    public CommentResponseDto.PostCommentDto getGuestBookComments(
            @PathVariable("guestbook_id") Long guestbookId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            HttpServletRequest request
    ){
        Member member = securityUtil.getUserFromHeader(request);
        List<CommentResponseDto.PostComment> comments = commentQueryService.getGuestBookComments(page, size, member, guestbookId);
        return CommentResponseDto.PostCommentDto.builder()
                .page((long) page)
                .size((long) size)
                .comment(comments)
                .build();
    }

    @PostMapping("/{guestbook_id}/comments")
    public ResponseEntity<PostResponseDto.SuccessResponseDto> createGuestBookComment(
            @PathVariable("guestbook_id") Long guestbookId,
            @RequestBody GuestBookRequestDto.GuestBookCommentDto guestBookCommentDto
    ){
        Member member = securityUtil.getUser();
        commentCommandService.createGuestBookComment(member, guestbookId, guestBookCommentDto);
        return new ResponseEntity<>(
                PostResponseDto.SuccessResponseDto.builder().message("댓글이 성공적으로 등록했습니다.").build(),
                HttpStatus.OK
        );
    }

    @PutMapping("/comments/{comment_id}")
    public ResponseEntity<PostResponseDto.SuccessResponseDto> modifyGuestBookComment(
            @PathVariable("comment_id") Long commentId,
            @RequestBody GuestBookRequestDto.GuestBookCommentDto guestBookCommentDto
    ){
        Member member = securityUtil.getUser();
        commentCommandService.modifyGuestBookComment(member, commentId, guestBookCommentDto.getContent());
        return new ResponseEntity<>(
                PostResponseDto.SuccessResponseDto.builder().message("댓글 성공적으로 수정했습니다.").build(),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/comments/{comment_id}")
    public ResponseEntity<PostResponseDto.SuccessResponseDto> deleteGuestBookComment(
            @PathVariable("comment_id") Long commentId
    ){
        Member member = securityUtil.getUser();
        commentCommandService.deleteGuestBookComment(member, commentId);
        return new ResponseEntity<>(
                PostResponseDto.SuccessResponseDto.builder().message("댓글 성공적으로 삭제했습니다.").build(),
                HttpStatus.OK
        );
    }


}
