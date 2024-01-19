package com.favoriteplace.app.controller;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.dto.community.PostResponseDto;
import com.favoriteplace.app.service.CommentService;
import com.favoriteplace.app.service.MemberService;
import com.favoriteplace.app.service.PostService;
import com.favoriteplace.global.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/free")
@RequiredArgsConstructor
public class PostController {
    private final MemberService memberService;
    private final PostService postService;
    private final CommentService commentService;
    private final SecurityUtil securityUtil;

    @GetMapping("/{post_id}")
    public PostResponseDto.PostDetailResponseDto getPostDetail(
            @PathVariable("post_id") Long postId,
            HttpServletRequest request
    ){
        return PostResponseDto.PostDetailResponseDto.builder()
                .userInfo(memberService.getUserInfoByPostId(postId))
                .postInfo(postService.getPostDetail(postId, request))
                .build();
    }

    @GetMapping("/{post_id}/comments")
    public PostResponseDto.PostCommentResponseDto getPostComments(
            @PathVariable("post_id") Long postId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ){
        return PostResponseDto.PostCommentResponseDto.builder()
                .size((long) size)
                .comment(commentService.getPostComments(page, size, postId))
                .build();
    }

    @GetMapping("/my-posts")
    public PostResponseDto.MyPostResponseDto getMyPosts(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ){
        return PostResponseDto.MyPostResponseDto.builder()
                .size((long)size)
                .post(postService.getMyPosts(page, size))
                .build();
    }


}
