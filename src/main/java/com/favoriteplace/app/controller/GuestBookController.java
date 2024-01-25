package com.favoriteplace.app.controller;

import com.favoriteplace.app.dto.community.GuestBookResponseDto;
import com.favoriteplace.app.service.CommentService;
import com.favoriteplace.app.service.GuestBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts/guestbooks")
@RequiredArgsConstructor
public class GuestBookController {
    private final GuestBookService guestBookService;
    private final CommentService commentService;

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
    public
}
