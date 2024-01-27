package com.favoriteplace.app.service;

import com.favoriteplace.app.domain.Image;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.community.Post;
import com.favoriteplace.app.dto.community.PostRequestDto;
import com.favoriteplace.app.repository.ImageRepository;
import com.favoriteplace.app.repository.PostRepository;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.gcpImage.UploadImage;
import com.favoriteplace.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostCommandService {
    private final SecurityUtil securityUtil;
    private final UploadImage uploadImage;
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;

    /**
     * 자유게시글 작성
     * @param data
     * @param images
     * @throws IOException
     */
    @Transactional
    public void createPost(PostRequestDto data, List<MultipartFile> images) throws IOException {
        Member member = securityUtil.getUser();
        Post newPost = Post.builder()
                .member(member).title(data.getTitle())
                .content(data.getContent()).likeCount(0L).view(0L)
                .build();
        postRepository.save(newPost);
        setImageList(newPost, images);
    }

    /**
     * 자유게사글 삭제
     * @param postId
     */
    @Transactional
    public void deletePost(long postId) {
        //TODO : 이미지, 해시테그 어떻게 관리할건지
        //securityUtil.getUser();
        Post post = postRepository.findById(postId).orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));
        postRepository.delete(post);
    }

    /**
     * 자유게시글 수정
     * @param postId
     * @param data
     * @param images
     */
    @Transactional
    public void modifyPost(long postId, PostRequestDto data, List<MultipartFile> images) throws IOException {
        Post post = postRepository.findById(postId).orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));
        Optional.ofNullable(data.getTitle()).ifPresent(post::setTitle);
        Optional.ofNullable(data.getContent()).ifPresent(post::setContent);

        //기존에 있던 이미지들 삭제
        imageRepository.deleteByPostId(postId);
        //새로 추가
        setImageList(post, images);
    }

    /**
     * 이미지가 여러개일 때, 이미지 처리하는 로직
     * @param newPost
     * @param images
     * @throws IOException
     */
    @Transactional
    public void setImageList(Post newPost, List<MultipartFile> images) throws IOException {
        //이미지 업로드 관련
        if(!images.isEmpty()){
            for(MultipartFile image: images){
                if(!image.isEmpty()){
                    String uuid = uploadImage.uploadImageToCloud(image);
                    Image newImage = Image.builder().url(uuid).post(newPost).build();
                    imageRepository.save(newImage);
                }
            }
        }
    }
}
