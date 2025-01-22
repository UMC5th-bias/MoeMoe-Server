package com.favoriteplace.app.community.service;

import com.favoriteplace.app.item.converter.PointHistoryConverter;
import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.community.domain.GuestBook;
import com.favoriteplace.app.community.domain.HashTag;
import com.favoriteplace.app.member.domain.enums.PointType;
import com.favoriteplace.app.pilgrimage.domain.Pilgrimage;
import com.favoriteplace.app.pilgrimage.domain.VisitedPilgrimage;
import com.favoriteplace.app.community.controller.dto.guestbook.GuestBookModifyRequestDto;
import com.favoriteplace.app.community.controller.dto.PostResponseDto;
import com.favoriteplace.app.community.repository.GuestBookRepository;
import com.favoriteplace.app.community.repository.HashtagRepository;
import com.favoriteplace.app.image.repository.ImageRepository;
import com.favoriteplace.app.community.repository.LikedPostRepository;
import com.favoriteplace.app.pilgrimage.repository.PilgrimageRepository;
import com.favoriteplace.app.item.repository.PointHistoryRepository;
import com.favoriteplace.app.pilgrimage.repository.VisitedPilgrimageRepository;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.gcpImage.UploadImage;
import com.favoriteplace.global.s3Image.AmazonS3ImageManager;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class GuestBookCommandService {
    private final GuestBookRepository guestBookRepository;
    private final ImageRepository imageRepository;
    private final LikedPostRepository likedPostRepository;
    private final UploadImage uploadImage;
    private final PilgrimageRepository pilgrimageRepository;
    private final HashtagRepository hashtagRepository;
    private final VisitedPilgrimageRepository visitedPilgrimageRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final AmazonS3ImageManager amazonS3ImageManager;

    /**
     * 성지순례 인증글 수정
     *
     * @param member
     * @param guestbookId
     * @param data
     * @param images
     */
    @Transactional
    public void modifyGuestBook(Member member, Long guestbookId, GuestBookModifyRequestDto data,
                                List<MultipartFile> images) throws IOException {
        GuestBook guestBook = guestBookRepository.findById(guestbookId)
                .orElseThrow(() -> new RestApiException(ErrorCode.GUESTBOOK_NOT_FOUND));
        checkAuthOfGuestBook(member, guestBook);
        Optional.ofNullable(data.title()).ifPresent(guestBook::setTitle);
        Optional.ofNullable(data.content()).ifPresent(guestBook::setContent);

        guestBook.getHashTags().clear();  //기존에 있던 hashtag 제거
        guestBook.getImages().clear();  //기존에 있던 이미지 제거
        imageRepository.deleteByGuestBookId(guestbookId);

        List<String> hashtags = data.hashtags();
        if (!hashtags.isEmpty()) {
            hashtags.forEach(hashtag -> guestBook.setHashTag(HashTag.builder().tagName(hashtag).build()));
        }

        // 새로운 이미지 등록
        try {
            List<String> imageUrls = amazonS3ImageManager.uploadMultiImages(images);
            guestBook.addImages(imageUrls);
        } catch (IOException e) {
            log.info("[guestBook image] image 없음");
        }
    }

    /**
     * 성지순례 인증글 삭제 (추천 목록도 삭제 필요)
     *
     * @param member
     * @param guestbookId
     */
    @Transactional
    public void deleteGuestBook(Member member, Long guestbookId) {
        GuestBook guestBook = guestBookRepository.findById(guestbookId)
                .orElseThrow(() -> new RestApiException(ErrorCode.GUESTBOOK_NOT_FOUND));
        checkAuthOfGuestBook(member, guestBook);
        likedPostRepository.deleteByGuestBookIdAndMemberId(guestBook.getId(), member.getId());
        guestBookRepository.deleteById(guestbookId);
    }

//    /**
//     * 이미지가 여러개일 때, 이미지 처리하는 로직 (새로운 이미지 저장)
//     * @param guestBook
//     * @param images
//     * @throws IOException
//     */
//    private void setImageList(GuestBook guestBook, List<MultipartFile> images) throws IOException {
//        List<CompletableFuture<Void>> futures = new ArrayList<>();
//        for(MultipartFile image:images){
//            if(image != null && !image.isEmpty()){
//                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//                    try {
//                        String uuid = uploadImage.uploadImageToCloud(image);
//                        Image newImage = Image.builder().url(ConvertUuidToUrl.convertUuidToUrl(uuid)).build();
//                        guestBook.setImage(newImage);
//                    } catch (IOException e) {
//                        throw new RestApiException(ErrorCode.IMAGE_CANNOT_UPLOAD);
//                    }
//                });
//                futures.add(future);
//            }
//        }
//        // 작업 다 끝날때 까지 기다림
//        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
//    }

    /**
     * 성지순례 인증글의 작성자가 맞는지 판단하는 함수
     *
     * @param member
     * @param guestBook
     */
    private void checkAuthOfGuestBook(Member member, GuestBook guestBook) {
        if (!member.getId().equals(guestBook.getMember().getId())) {
            throw new RestApiException(ErrorCode.USER_NOT_AUTHOR);
        }
    }

    /**
     * 성지순례 방문 인증글 작성
     *
     * @param member       인증한 사용자
     * @param pilgrimageId 성지순례 아이디
     * @param data         json 폼
     * @param images       이미지
     * @return
     * @throws IOException
     */
    @Transactional
    public PostResponseDto.GuestBookIdResponseDto postGuestBook(Member member, Long pilgrimageId,
                                                                GuestBookModifyRequestDto data,
                                                                List<MultipartFile> images) throws IOException {
        Pilgrimage pilgrimage = pilgrimageRepository
                .findById(pilgrimageId).orElseThrow(() -> new RestApiException(ErrorCode.PILGRIMAGE_NOT_FOUND));

        if (images == null || images.isEmpty()) {
            throw new RestApiException(ErrorCode.GUESTBOOK_MUST_INCLUDE_IMAGES);
        }
        if (!images.stream().anyMatch(file -> !file.isEmpty())) {
            throw new RestApiException(ErrorCode.GUESTBOOK_MUST_INCLUDE_IMAGES);
        }

        checkVisited(pilgrimage, member);
        GuestBook newGuestBook = saveGuestBook(member, data, pilgrimage);

        data.hashtags().stream().forEach(hashTag -> {
            HashTag newHashTag = HashTag.builder().tagName(hashTag).guestBook(newGuestBook).build();
            hashtagRepository.save(newHashTag);
            newGuestBook.setHashTag(newHashTag);
        });

//        if (images != null && !images.isEmpty()) {
//            setImageList(newGuestBook, images);
//        }
        // 새로운 이미지 등록
        try {
            List<String> imageUrls = amazonS3ImageManager.uploadMultiImages(images);
            newGuestBook.addImages(imageUrls);
        } catch (IOException e) {
            log.info("[guestBook image] image 없음");
        }
        log.info("success image upload");

        successPostAndPointProcess(member, pilgrimage);
        log.info("success point update");

        return PostResponseDto.GuestBookIdResponseDto.builder().guestBookId(newGuestBook.getId()).build();
    }

    private void checkVisited(Pilgrimage pilgrimage, Member member) {
        List<VisitedPilgrimage> visitedPilgrimageList = visitedPilgrimageRepository.findByPilgrimageAndMemberOrderByCreatedAtDesc(
                pilgrimage, member);

        boolean hasVisited = visitedPilgrimageList.stream()
                .anyMatch(visitedPilgrimage -> pilgrimage.getId().equals(visitedPilgrimage.getPilgrimage().getId()));

        if (!hasVisited) {
            throw new RestApiException(ErrorCode.PILGRIMAGE_NOT_CERTIFIED);
        }
    }

    private GuestBook saveGuestBook(Member member, GuestBookModifyRequestDto data, Pilgrimage pilgrimage) {
        GuestBook guestBook = GuestBook.builder()
                .member(member)
                .pilgrimage(pilgrimage)
                .title(data.title())
                .content(data.content())
                .likeCount(0L)
                .view(0L)
                .build();
        return guestBookRepository.save(guestBook);
    }

    public void successPostAndPointProcess(Member member, Pilgrimage pilgrimage) {
        VisitedPilgrimage newVisited = VisitedPilgrimage.builder().pilgrimage(pilgrimage).member(member).build();
        visitedPilgrimageRepository.save(newVisited);
        pointHistoryRepository.save(PointHistoryConverter.toPointHistory(member, 20L, PointType.ACQUIRE));
        member.updatePoint(20L);
    }

    /**
     * 성지 순례 인증글 조회수 증가
     *
     * @param guestBookId
     */
    @Transactional
    public void increaseGuestBookView(Long guestBookId) {
        Optional<GuestBook> optionalGuestBook = guestBookRepository.findById(guestBookId);
        if (optionalGuestBook.isEmpty()) {
            throw new RestApiException(ErrorCode.GUESTBOOK_NOT_FOUND);
        }
        GuestBook guestBook = optionalGuestBook.get();
        guestBook.increaseView();
        guestBookRepository.save(guestBook);
    }
}
