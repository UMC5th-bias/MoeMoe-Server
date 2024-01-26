package com.favoriteplace.app.controller;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.dto.community.CommentResponseDto;
import com.favoriteplace.app.dto.community.GuestBookResponseDto;
import com.favoriteplace.app.service.CommentService;
import com.favoriteplace.app.service.GuestBookService;
import com.favoriteplace.app.service.MemberService;
import com.favoriteplace.app.service.PilgrimageQueryService;
import com.favoriteplace.global.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/guestbooks")
@RequiredArgsConstructor
public class GuestBookController {
    private final GuestBookService guestBookService;
    private final CommentService commentService;
    private final MemberService memberService;
    private final PilgrimageQueryService pilgrimageQueryService;
    private final SecurityUtil securityUtil;


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
        Page<GuestBookResponseDto.GuestBook> myGuestBooks = guestBookService.getMyGuestBooks(page, size);
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
        guestBookService.increaseGuestBookView(guestBookId);
        return GuestBookResponseDto.DetailGuestBookDto.builder()
                .userInfo(memberService.getUserInfoByGuestBookId(guestBookId))
                .pilgrimage(pilgrimageQueryService.getPilgrimageDetailCommunity(member, guestBookId))
                .guestBook(guestBookService.getDetailGuestBookInfo(guestBookId, request))
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
}
