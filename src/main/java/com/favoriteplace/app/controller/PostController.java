package com.favoriteplace.app.controller;

import com.favoriteplace.app.dto.community.CommentRequestDto;
import com.favoriteplace.app.dto.community.CommentResponseDto;
import com.favoriteplace.app.dto.community.PostRequestDto;
import com.favoriteplace.app.dto.community.PostResponseDto;
import com.favoriteplace.app.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
    private final CommentService commentService;
    private final LikedPostService likedPostService;

    @GetMapping("/{post_id}")
    public PostResponseDto.PostDetailResponseDto getPostDetail(
            @PathVariable("post_id") Long postId,
            HttpServletRequest request
    ){
        postQueryService.increasePostView(postId);
        return PostResponseDto.PostDetailResponseDto.builder()
                .userInfo(memberService.getUserInfoByPostId(postId))
                .postInfo(postQueryService.getPostDetail(postId, request))
                .build();
    }

    @GetMapping("/{post_id}/comments")
    public CommentResponseDto.PostCommentDto getPostComments(
            @PathVariable("post_id") Long postId,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ){
        return CommentResponseDto.PostCommentDto.builder()
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
                .post(postQueryService.getMyPosts(page, size))
                .build();
    }

    @GetMapping("/my-comments")
    public PostResponseDto.MyCommentDto getMyComments(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size
    ){
        return PostResponseDto.MyCommentDto.builder()
                .size((long) size)
                .comment(commentService.getMyPostComments(page, size))
                .build();
    }

    @GetMapping("")
    public PostResponseDto.MyPostResponseDto getTotalPost(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "latest") String sort
    ){
        return PostResponseDto.MyPostResponseDto.builder()
                .size((long)size)
                .post(postQueryService.getTotalPostBySort(page, size, sort))
                .build();
    }

    @PostMapping("")
    public ResponseEntity<PostResponseDto.SuccessResponseDto> createPost(
            @RequestPart PostRequestDto data,
            @RequestPart(required = false) List<MultipartFile> images
    ) throws IOException {
        postCommandService.createPost(data, images);
        return new ResponseEntity<>(
                PostResponseDto.SuccessResponseDto.builder().message("게시글을 성공적으로 등록했습니다.").build(),
                HttpStatus.OK);
    }

    @DeleteMapping("/{post_id}")
    public ResponseEntity<PostResponseDto.SuccessResponseDto> deletePost(
            @PathVariable("post_id") long postId
    ){
        postCommandService.deletePost(postId);
        return new ResponseEntity<>(
                PostResponseDto.SuccessResponseDto.builder().message("게시글이 성공적으로 삭제되었습니다.").build(),
                HttpStatus.OK
        );
    }

    @PostMapping("/{post_id}")
    public ResponseEntity<PostResponseDto.SuccessResponseDto> createComment(
            @PathVariable("post_id") long postId,
            @RequestBody CommentRequestDto dto
    ){
        commentService.createComment(postId, dto.getContent());
        return new ResponseEntity<>(
                PostResponseDto.SuccessResponseDto.builder().message("댓글을 성공적으로 등록했습니다.").build(),
                HttpStatus.OK
        );
    }

    @PutMapping("/{post_id}/like")
    public ResponseEntity<PostResponseDto.SuccessResponseDto> modifyPostLike(
            @PathVariable("post_id") long postId
    ){
        String message = likedPostService.modifyPostLike(postId);
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
        postCommandService.modifyPost(postId, data, images);
        return new ResponseEntity<>(
                PostResponseDto.SuccessResponseDto.builder().message("게시글이 수정되었습니다.").build(),
                HttpStatus.OK
        );
    }
}
