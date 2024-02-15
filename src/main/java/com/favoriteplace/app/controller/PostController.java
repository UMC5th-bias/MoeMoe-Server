package com.favoriteplace.app.controller;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.dto.community.PostRequestDto;
import com.favoriteplace.app.dto.community.PostResponseDto;
import com.favoriteplace.app.service.*;
import com.favoriteplace.app.service.community.LikedPostService;
import com.favoriteplace.app.service.community.PostCommandService;
import com.favoriteplace.app.service.community.PostQueryService;
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
@RequestMapping("/posts/free")
@RequiredArgsConstructor
public class PostController {
    private final MemberService memberService;
    private final PostQueryService postQueryService;
    private final PostCommandService postCommandService;
    private final LikedPostService likedPostService;
    private final SecurityUtil securityUtil;

    @GetMapping("/{post_id}")
    public PostResponseDto.PostDetailResponseDto getPostDetail(
            @PathVariable("post_id") Long postId,
            HttpServletRequest request
    ){
        postCommandService.increasePostView(postId);
        return postQueryService.getPostDetail(postId, request);
    }

    @GetMapping("/my-posts")
    public PostResponseDto.MyPostResponseDto getMyPosts(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ){
        Member member = securityUtil.getUser();
        List<PostResponseDto.MyPost> posts = postQueryService.getMyPosts(member, page, size);
        return PostResponseDto.MyPostResponseDto.builder()
                .page((long) page)
                .size((long) size)
                .post(posts)
                .build();
    }

    @GetMapping("")
    public PostResponseDto.MyPostResponseDto getTotalPost(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "latest") String sort
    ){
        List<PostResponseDto.MyPost> sortPages = postQueryService.getTotalPostBySort(page, size, sort);
        return PostResponseDto.MyPostResponseDto.builder()
                .page((long) page)
                .size((long) size)
                .post(sortPages)
                .build();
    }

    @GetMapping("/search")
    public PostResponseDto.MyPostResponseDto getTotalPostByKeyword(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam() String searchType,
            @RequestParam() String keyword
    ){
        Page<PostResponseDto.MyPost> posts = postQueryService.getTotalPostByKeyword(page, size, searchType, keyword);
        return PostResponseDto.MyPostResponseDto.builder()
                .page((long) (posts.getNumber()+1))
                .size((long) posts.getSize())
                .post(posts.getContent())
                .build();
    }

    @PostMapping("")
    public ResponseEntity<PostResponseDto.SuccessResponseDto> createPost(
            @RequestPart PostRequestDto data,
            @RequestPart(required = false) List<MultipartFile> images
    ) throws IOException {
        Member member = securityUtil.getUser();
        postCommandService.createPost(data, images, member);
        return new ResponseEntity<>(
                PostResponseDto.SuccessResponseDto.builder().message("게시글을 성공적으로 등록했습니다.").build(),
                HttpStatus.OK);
    }

    @DeleteMapping("/{post_id}")
    public ResponseEntity<PostResponseDto.SuccessResponseDto> deletePost(
            @PathVariable("post_id") long postId
    ){
        Member member = securityUtil.getUser();
        postCommandService.deletePost(postId, member);
        return new ResponseEntity<>(
                PostResponseDto.SuccessResponseDto.builder().message("게시글이 성공적으로 삭제되었습니다.").build(),
                HttpStatus.OK
        );
    }

    @PutMapping("/{post_id}/like")
    public ResponseEntity<PostResponseDto.SuccessResponseDto> modifyPostLike(
            @PathVariable("post_id") long postId
    ){
        Member member = securityUtil.getUser();
        String message = likedPostService.modifyPostLike(member, postId);
        return new ResponseEntity<>(
                PostResponseDto.SuccessResponseDto.builder().message(message).build(),
                HttpStatus.OK
        );
    }

    @PatchMapping("/{post_id}")
    public ResponseEntity<PostResponseDto.SuccessResponseDto> modifyPost(
            @PathVariable("post_id") Long postId,
            @RequestPart PostRequestDto data,
            @RequestPart(required = false) List<MultipartFile> images
    ) throws IOException {
        Member member = securityUtil.getUser();
        postCommandService.modifyPost(postId, data, images, member);
        return new ResponseEntity<>(
                PostResponseDto.SuccessResponseDto.builder().message("게시글이 수정되었습니다.").build(),
                HttpStatus.OK
        );
    }
}
