package com.favoriteplace.app.service.community;

import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.dto.community.PostRequestDto;
import com.favoriteplace.app.repository.ImageRepository;
import com.favoriteplace.app.repository.LikedPostRepository;
import com.favoriteplace.app.repository.PostRepository;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.s3Image.AmazonS3ImageManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostCommandService {
    private final PostRepository postRepository;
    private final LikedPostRepository likedPostRepository;
    private final ImageRepository imageRepository;
    private final AmazonS3ImageManager amazonS3ImageManager;

    /**
     * 자유게시글 작성
     *
     * @param data
     * @param images
     * @throws IOException
     */
    @Transactional
    public Long createPost(PostRequestDto data, List<MultipartFile> images, Member member) throws IOException {
        Post newPost = Post.builder()
                .member(member).title(data.title())
                .images(new ArrayList<>())
                .content(data.content()).likeCount(0L).view(0L)
                .build();

        try {
            List<String> imageUrls = amazonS3ImageManager.uploadMultiImages(images);
            newPost.addImages(imageUrls);
        } catch (IOException e) {
            log.info("[post image] image 없음");
        }

        Post post = postRepository.save(newPost);
        return post.getId();
    }

    /**
     * 자유게사글 삭제
     *
     * @param postId
     */
    @Transactional
    public void deletePost(long postId, Member member) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));
        checkAuthOfGuestBook(member, post);
        likedPostRepository.deleteByPostIdAndMemberId(postId, member.getId());
        postRepository.delete(post);
    }

    /**
     * 자유게시글 수정
     *
     * @param postId
     * @param data
     * @param images
     */
    @Transactional
    public void modifyPost(
            long postId,
            PostRequestDto data,
            List<MultipartFile> images,
            Member member
    ) throws IOException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));
        checkAuthOfGuestBook(member, post);
        Optional.ofNullable(data.title()).ifPresent(post::setTitle);
        Optional.ofNullable(data.content()).ifPresent(post::setContent);

        //기존의 이미지 삭제 필요
        post.getImages().clear();
        imageRepository.deleteByPostId(post.getId());

        // 새로운 이미지 등록
        try {
            List<String> imageUrls = amazonS3ImageManager.uploadMultiImages(images);
            post.addImages(imageUrls);
        } catch (IOException e) {
            log.info("[post image] image 없음");
        }

    }

    /**
     * 게시글의 조회수를 증가하는 함수
     *
     * @param postId
     */
    @Transactional
    public void increasePostView(Long postId) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isEmpty()) {
            throw new RestApiException(ErrorCode.POST_NOT_FOUND);
        }
        Post post = postOptional.get();
        post.increaseView();
        postRepository.save(post);
    }

    /**
     * post의 작성자가 맞는지 확인하는 로직
     *
     * @param member
     * @param post
     */
    private void checkAuthOfGuestBook(Member member, Post post) {
        if (!member.getId().equals(post.getMember().getId())) {
            throw new RestApiException(ErrorCode.USER_NOT_AUTHOR);
        }
    }
}
