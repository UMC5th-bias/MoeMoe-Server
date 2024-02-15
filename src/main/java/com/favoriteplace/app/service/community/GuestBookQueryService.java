package com.favoriteplace.app.service.community;

import com.favoriteplace.app.converter.GuestBookConverter;
import com.favoriteplace.app.domain.Image;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.domain.community.HashTag;
import com.favoriteplace.app.dto.community.GuestBookResponseDto;
import com.favoriteplace.app.dto.community.TrendingPostResponseDto;
import com.favoriteplace.app.repository.GuestBookRepository;
import com.favoriteplace.app.repository.HashtagRepository;
import com.favoriteplace.app.repository.ImageRepository;
import com.favoriteplace.app.repository.LikedPostRepository;
import com.favoriteplace.app.service.community.searchStrategy.SearchGuestBookByContent;
import com.favoriteplace.app.service.community.searchStrategy.SearchGuestBookByNickname;
import com.favoriteplace.app.service.community.searchStrategy.SearchGuestBookByTitle;
import com.favoriteplace.app.service.community.searchStrategy.SearchStrategy;
import com.favoriteplace.app.service.community.sortStrategy.SortGuestBookByLatestStrategy;
import com.favoriteplace.app.service.community.sortStrategy.SortGuestBookByLikedStrategy;
import com.favoriteplace.app.service.community.sortStrategy.SortStrategy;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuestBookQueryService {
    private final GuestBookRepository guestBookRepository;
    private final LikedPostRepository likedPostRepository;
    private final ImageRepository imageRepository;
    private final HashtagRepository hashtagRepository;
    private final SortGuestBookByLatestStrategy sortGuestBookByLatestStrategy;
    private final SortGuestBookByLikedStrategy sortGuestBookByLikedStrategy;
    private final SearchGuestBookByTitle searchGuestBookByTitle;
    private final SearchGuestBookByNickname searchGuestBookByNickname;
    private final SearchGuestBookByContent searchGuestBookByContent;
    private final CountCommentsService countCommentsService;
    private final SecurityUtil securityUtil;

    public List<TrendingPostResponseDto.TrendingPostRank> getTodayTrendingGuestBook() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        List<GuestBook> guestBooks = guestBookRepository.findByCreatedAtBetweenOrderByLikeCountDesc(startOfDay, now);
        if(guestBooks.isEmpty()){
            throw new RestApiException(ErrorCode.GUESTBOOK_NOT_FOUND);
        }
        guestBooks.subList(0, Math.min(5, guestBooks.size()));

        List<TrendingPostResponseDto.TrendingPostRank> trendingPostsRank = new ArrayList<>();
        for(int i = 0; (i < guestBooks.size()) && (i < 5); i++){
            trendingPostsRank.add(TrendingPostResponseDto.TrendingPostRank.of(guestBooks.get(i)));
        }
        return trendingPostsRank;
    }

    public Page<GuestBookResponseDto.MyGuestBookInfo> getMyGuestBooks(int page, int size) {
        Member member = securityUtil.getUser();
        Pageable pageable = PageRequest.of(page-1, size);
        Page<GuestBook> myGuestBooks = guestBookRepository.findAllByMemberIdOrderByCreatedAtDesc(member.getId(), pageable);
        if(myGuestBooks.isEmpty()){return Page.empty();}
        return myGuestBooks.map(guestBook -> GuestBookConverter.toGuestBook(guestBook, member.getNickname(), countCommentsService.countGuestBookComments(guestBook.getId())));
    }

    public GuestBookResponseDto.GuestBookInfo getDetailGuestBookInfo(Long guestBookId, HttpServletRequest request) {
        Member member = securityUtil.getUserFromHeader(request);
        Optional<GuestBook> optionalGuestBook = guestBookRepository.findById(guestBookId);
        if(optionalGuestBook.isEmpty()){throw new RestApiException(ErrorCode.GUESTBOOK_NOT_FOUND);}
        //GuestBook
        GuestBook guestBook = optionalGuestBook.get();
        //Image
        List<Image> images = imageRepository.findAllByGuestBookId(guestBook.getId());
        List<String> imagesUrl = images.stream().map(Image::getUrl).toList();
        //HashTag
        List<HashTag> hashTags = hashtagRepository.findAllByGuestBookId(guestBook.getId());
        List<String> hashTagsString = hashTags.stream().map(HashTag::getTagName).toList();
        if(!securityUtil.isTokenExists(request)){
            return GuestBookConverter.toGuestBookInfo(guestBook, false, false, imagesUrl, hashTagsString);
        }
        return GuestBookConverter.toGuestBookInfo(guestBook, isLiked(guestBook.getId(), member.getId()), isWriter(guestBook.getId(), member.getId()), imagesUrl, hashTagsString);
    }

    /**
     * sort에 따라 전체 정시순례 인증글들을 페이징해서 보여주는 기능
     * @param page
     * @param size
     * @param sort
     * @return sort에 따라 전체 정시순례 인증글들
     */
    public Page<GuestBookResponseDto.TotalGuestBookInfo> getTotalGuestBooksBySort(int page, int size, String sort) {
        Pageable pageable = PageRequest.of(page-1, size);
        SortStrategy<GuestBook> sortStrategy;
        if("latest".equals(sort)){
            sortStrategy = sortGuestBookByLatestStrategy;
        }
        else if("liked".equals(sort)){
            sortStrategy = sortGuestBookByLikedStrategy;
        }else{
            throw new RestApiException(ErrorCode.SORT_TYPE_NOT_ALLOWED);
        }
        Page<GuestBook> guestBooks = sortStrategy.sort(pageable);
        if(guestBooks.isEmpty()){return Page.empty();}
        return guestBooks.map(GuestBookConverter::toTotalGuestBookInfo);
    }

    /**
     * searchType(제목, 닉네임, 내용)을 기반으로 성지순레 인증글을 가져오는 함 (게시글 생성일의 내림차순으로 정렬)
     * @param page
     * @param size
     * @param searchType
     * @param keyword
     * @return
     */
    public Page<GuestBookResponseDto.TotalGuestBookInfo> getTotalPostByKeyword(int page, int size, String searchType, String keyword) {
        Pageable pageable = PageRequest.of(page-1, size);
        SearchStrategy<GuestBook> searchStrategy;
        if("title".equals(searchType)){
            searchStrategy = searchGuestBookByTitle;
        } else if ("nickname".equals(searchType)) {
            searchStrategy = searchGuestBookByNickname;
        } else if ("content".equals(searchType)) {
            searchStrategy = searchGuestBookByContent;
        } else {
            throw new RestApiException(ErrorCode.SEARCH_TYPE_NOT_ALLOWED);
        }
        if(keyword.trim().isEmpty()){return Page.empty();}
        Page<GuestBook> guestBooks = searchStrategy.search(keyword, pageable);
        if(guestBooks.isEmpty()){return Page.empty();}
        return guestBooks.map(GuestBookConverter::toTotalGuestBookInfo);
    }

    private Long countGuestBookComment(GuestBook guestBook) {
        return (long) guestBook.getComments().size();
    }


    private Boolean isLiked(Long guestBookId, Long memberId){
        return likedPostRepository.existsByGuestBookIdAndMemberId(guestBookId, memberId);
    }

    private Boolean isWriter(Long guestBookId, Long memberId){
        Optional<GuestBook> optionalGuestBook = guestBookRepository.findById(guestBookId);
        if(optionalGuestBook.isEmpty()){
            throw new RestApiException(ErrorCode.GUESTBOOK_NOT_FOUND);
        }
        return optionalGuestBook.get().getMember().getId().equals(memberId);
    }

    public void increaseGuestBookView(Long guestBookId) {
        Optional<GuestBook> optionalGuestBook = guestBookRepository.findById(guestBookId);
        if(optionalGuestBook.isEmpty()){throw new RestApiException(ErrorCode.GUESTBOOK_NOT_FOUND);}
        GuestBook guestBook = optionalGuestBook.get();
        guestBook.increaseView();
        guestBookRepository.save(guestBook);
    }


}
