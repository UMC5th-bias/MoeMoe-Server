package com.favoriteplace.app.controller;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.dto.community.*;
import com.favoriteplace.app.service.*;
import com.favoriteplace.global.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/posts/guestbooks")
@RequiredArgsConstructor
public class GuestBookController {
    private final GuestBookQueryService guestBookQueryService;
    private final GuestBookCommandService guestBookCommandService;
    private final CommentService commentService;
    private final MemberService memberService;
    private final PilgrimageQueryService pilgrimageQueryService;
    private final SecurityUtil securityUtil;

    @GetMapping()
    public GuestBookResponseDto.TotalGuestBookDto getTotalGuestBooks(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "latest") String sort
    ){
        Page<GuestBookResponseDto.TotalGuestBookInfo> guestBookInfos = guestBookQueryService.getTotalGuestBooksBySort(page, size, sort);
        return GuestBookResponseDto.TotalGuestBookDto.builder()
                .page((long)guestBookInfos.getNumber()+1)
                .size((long)guestBookInfos.getSize())
                .guestBook(guestBookInfos.getContent())
                .build();
    }

    @GetMapping("/my-comments")
    public GuestBookResponseDto.MyGuestBookCommentDto getMyComments(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ){
        Page<GuestBookResponseDto.MyGuestBookComment> myComments = commentService.getMyGuestBookComments(page, size);
        return GuestBookResponseDto.MyGuestBookCommentDto.builder()
                .page((long) (myComments.getNumber()+1))
                .size((long) myComments.getSize())
                .comment(myComments.getContent())
                .build();
    }

    @GetMapping("/my-posts")
    public GuestBookResponseDto.MyGuestBookDto getMyPosts(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ){
        Page<GuestBookResponseDto.GuestBook> myGuestBooks = guestBookQueryService.getMyGuestBooks(page, size);
        return GuestBookResponseDto.MyGuestBookDto.builder()
                .page((long)myGuestBooks.getNumber() + 1)
                .size((long)myGuestBooks.getSize())
                .guestBook(myGuestBooks.getContent())
                .build();
    }

    @GetMapping("/{guestbook_id}")
    public GuestBookResponseDto.DetailGuestBookDto getDetailGuestBook(
            @PathVariable("guestbook_id") Long guestBookId,
            HttpServletRequest request
    ){
        Member member = securityUtil.getUserFromHeader(request);
        guestBookQueryService.increaseGuestBookView(guestBookId);
        return GuestBookResponseDto.DetailGuestBookDto.builder()
                .userInfo(memberService.getUserInfoByGuestBookId(guestBookId))
                .pilgrimage(pilgrimageQueryService.getPilgrimageDetailCommunity(member, guestBookId))
                .guestBook(guestBookQueryService.getDetailGuestBookInfo(guestBookId, request))
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
        Page<CommentResponseDto.PostComment> comments = commentService.getGuestBookComments(page, size, member, guestbookId);
        return CommentResponseDto.PostCommentDto.builder()
                .page((long) comments.getNumber() +1)
                .size((long) comments.getSize())
                .comment(comments.getContent())
                .build();
    }

    @PatchMapping("/{guestbook_id}")
    public ResponseEntity<PostResponseDto.SuccessResponseDto> modifyGuestBook(
            @PathVariable("guestbook_id") Long guestbookId,
            @RequestPart GuestBookRequestDto.ModifyGuestBookDto data,
            @RequestPart(required = false) List<MultipartFile> images
    ) throws IOException {
        Member member = securityUtil.getUser();
        guestBookCommandService.modifyGuestBook(member, guestbookId, data, images);
        return new ResponseEntity<>(
                PostResponseDto.SuccessResponseDto.builder().message("성지순례 인증글을 성공적으로 수정했습니다.").build(),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{guestbook_id}")
    public ResponseEntity<PostResponseDto.SuccessResponseDto> deleteGuestBook(
            @PathVariable("guestbook_id") Long guestbookId
    ){
        Member member = securityUtil.getUser();
        guestBookCommandService.deleteGuestBook(member, guestbookId);
        return new ResponseEntity<>(
                PostResponseDto.SuccessResponseDto.builder().message("성지순례 인증글을 성공적으로 삭제했습니다.").build(),
                HttpStatus.OK
        );
    }

    @PostMapping("/{guestbook_id}")
    public ResponseEntity<PostResponseDto.SuccessResponseDto> createGuestBookComment(
            @PathVariable("guestbook_id") Long guestbookId,
            @RequestBody GuestBookRequestDto.GuestBookCommentDto guestBookCommentDto
    ){
        Member member = securityUtil.getUser();
        guestBookCommandService.createGuestBookComment(member, guestbookId, guestBookCommentDto);
        return new ResponseEntity<>(
                PostResponseDto.SuccessResponseDto.builder().message("성지순례 인증글에 댓글이 성공적으로 추가되었습니다.").build(),
                HttpStatus.OK
        );
    }
}
