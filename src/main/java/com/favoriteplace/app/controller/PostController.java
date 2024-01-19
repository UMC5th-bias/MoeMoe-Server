package com.favoriteplace.app.controller;

import com.favoriteplace.app.dto.community.PostResponseDto;
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


}
