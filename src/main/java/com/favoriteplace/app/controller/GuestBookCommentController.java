package com.favoriteplace.app.controller;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.dto.community.comment.CommentCreateRequestDto;
import com.favoriteplace.app.dto.community.comment.CommentModifyRequestDto;
import com.favoriteplace.app.dto.community.GuestBookResponseDto;
import com.favoriteplace.app.dto.community.PostResponseDto;
import com.favoriteplace.app.dto.community.comment.CommentRootResponseDto;
import com.favoriteplace.app.service.community.CommentCommandService;
import com.favoriteplace.app.service.community.CommentQueryService;
import com.favoriteplace.global.util.SecurityUtil;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/guestbooks")
@RequiredArgsConstructor
public class GuestBookCommentController {
    private final SecurityUtil securityUtil;
    private final CommentQueryService commentQueryService;
    private final CommentCommandService commentCommandService;

    @GetMapping("/my-comments")
    public ResponseEntity<GuestBookResponseDto.MyGuestBookCommentDto> getMyComments(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ){
        Member member = securityUtil.getUser();
        return ResponseEntity.ok(commentQueryService.getMyGuestBookComments(member, page, size));
    }

    @GetMapping("/{guestbook_id}/comments")
    public ResponseEntity<CommentRootResponseDto> getGuestBookComments(
            @PathVariable("guestbook_id") Long guestbookId,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "5") int size,
            HttpServletRequest request
    ){
        Member member = securityUtil.getUserFromHeader(request);
        return ResponseEntity.ok(commentQueryService.getGuestBookComments(page, size, member, guestbookId));
    }

    @PostMapping("/{guestbook_id}/comments")
    public ResponseEntity<PostResponseDto.CommentSuccessResponseDto> createGuestBookComment(
            @PathVariable("guestbook_id") Long guestbookId,
            @RequestBody CommentCreateRequestDto guestBookCommentDto
    ){
        Member member = securityUtil.getUser();
        Long commentId = commentCommandService.createGuestBookComment(member, guestbookId, guestBookCommentDto);
        return new ResponseEntity<>(
                PostResponseDto.CommentSuccessResponseDto.builder().commentId(commentId).build(),
                HttpStatus.OK
        );
    }

    @PostMapping("/{guestbook_id}/comments/{comment_id}/notification")
    public ResponseEntity<Void> sendGuestBookNotification(
            @PathVariable("guestbook_id") Long guestbookId,
            @PathVariable("comment_id") Long commentId
    ){
        commentCommandService.sendGuestBookNotification(guestbookId, commentId);
        return ResponseEntity.ok().build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "204")
    })
    @PutMapping("/comments/{comment_id}")
    public ResponseEntity<Void> modifyGuestBookComment(
            @PathVariable("comment_id") Long commentId,
            @RequestBody CommentModifyRequestDto dto
    ){
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
    public ResponseEntity<Void> deleteGuestBookComment(
            @PathVariable("comment_id") Long commentId
    ){
        Member member = securityUtil.getUser();
        commentCommandService.deleteComment(member, commentId);
        return new ResponseEntity<>(
                HttpStatus.NO_CONTENT
        );
    }


}
