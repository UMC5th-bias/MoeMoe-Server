package com.favoriteplace.app.service.community;

import com.favoriteplace.app.domain.Image;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.community.Comment;
import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.domain.community.HashTag;
import com.favoriteplace.app.domain.community.LikedPost;
import com.favoriteplace.app.dto.community.GuestBookRequestDto;
import com.favoriteplace.app.repository.GuestBookRepository;
import com.favoriteplace.app.repository.HashtagRepository;
import com.favoriteplace.app.repository.ImageRepository;
import com.favoriteplace.app.repository.LikedPostRepository;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.gcpImage.ConvertUuidToUrl;
import com.favoriteplace.global.gcpImage.UploadImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GuestBookCommandService {
    private final GuestBookRepository guestBookRepository;
    private final ImageRepository imageRepository;
    private final LikedPostRepository likedPostRepository;
    private final UploadImage uploadImage;

    /**
     * 성지순례 인증글 수정
     * @param member
     * @param guestbookId
     * @param data
     * @param images
     */
    @Transactional
    public void modifyGuestBook(Member member, Long guestbookId, GuestBookRequestDto.ModifyGuestBookDto data, List<MultipartFile> images) throws IOException {
        GuestBook guestBook = guestBookRepository.findById(guestbookId).orElseThrow(() -> new RestApiException(ErrorCode.GUESTBOOK_NOT_FOUND));
        checkAuthOfGuestBook(member, guestBook);
        Optional.ofNullable(data.getTitle()).ifPresent(guestBook::setTitle);
        Optional.ofNullable(data.getContent()).ifPresent(guestBook::setContent);
        guestBook.getHashTags().clear();  //기존에 있던 hashtag 제거
        guestBook.getImages().clear();  //기존에 있던 이미지 제거
        imageRepository.deleteByGuestBookId(guestbookId);
        List<String> hashtags = data.getHashtags();
        if(!hashtags.isEmpty()){
            hashtags.forEach(hashtag -> guestBook.setHashTag(HashTag.builder().tagName(hashtag).build()));
        }
        setImageList(guestBook, images);
        guestBookRepository.save(guestBook);
    }

    /**
     * 성지순례 인증글 삭제 (추천 목록도 삭제 필요)
     * @param member
     * @param guestbookId
     */
    @Transactional
    public void deleteGuestBook(Member member, Long guestbookId) {
        GuestBook guestBook = guestBookRepository.findById(guestbookId).orElseThrow(() -> new RestApiException(ErrorCode.GUESTBOOK_NOT_FOUND));
        checkAuthOfGuestBook(member, guestBook);
        likedPostRepository.deleteByGuestBookIdAndMemberId(guestBook.getId(), member.getId());
        guestBookRepository.deleteById(guestbookId);
    }

    /**
     * 성지순례 인증글에 댓글 추가
     * @param member
     * @param guestbookId
     */
    @Transactional
    public void createGuestBookComment(Member member, Long guestbookId, GuestBookRequestDto.GuestBookCommentDto comment) {
        GuestBook guestBook = guestBookRepository.findById(guestbookId).orElseThrow(() -> new RestApiException(ErrorCode.GUESTBOOK_NOT_FOUND));
        Comment newComment = Comment.builder().member(member).guestBook(guestBook).content(comment.getContent()).build();
        guestBook.addComment(newComment);
        guestBookRepository.save(guestBook);
    }

    /**
     * 이미지가 여러개일 때, 이미지 처리하는 로직 (새로운 이미지 저장)
     * @param guestBook
     * @param images
     * @throws IOException
     */
    private void setImageList(GuestBook guestBook, List<MultipartFile> images) throws IOException {
        if(images != null){
            for(MultipartFile image:images){
                if(!image.isEmpty()){
                    String uuid = uploadImage.uploadImageToCloud(image);
                    Image newImage = Image.builder().url(ConvertUuidToUrl.convertUuidToUrl(uuid)).build();
                    guestBook.setImage(newImage);
                }
            }
        }
    }

    /**
     * 성지순례 인증글의 작성자가 맞는지 판단하는 함수
     * @param member
     * @param guestBook
     */
    private void checkAuthOfGuestBook(Member member, GuestBook guestBook){
        if(!member.getId().equals(guestBook.getMember().getId())){
            throw new RestApiException(ErrorCode.USER_NOT_AUTHOR);
        }
    }


}
