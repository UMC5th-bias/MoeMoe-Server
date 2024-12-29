package com.favoriteplace.app.service.community;

import com.favoriteplace.app.converter.GuestBookConverter;
import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.community.GuestBook;
import com.favoriteplace.app.domain.travel.Pilgrimage;
import com.favoriteplace.app.dto.community.guestbook.GuestBookResponseDto;
import com.favoriteplace.app.dto.community.TrendingPostResponseDto;
import com.favoriteplace.app.repository.GuestBookImplRepository;
import com.favoriteplace.app.repository.LikedPostRepository;
import com.favoriteplace.app.repository.PilgrimageRepository;
import com.favoriteplace.app.repository.VisitedPilgrimageRepository;
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

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GuestBookQueryService {
    private final GuestBookImplRepository guestBookImplRepository;
    private final LikedPostRepository likedPostRepository;
    private final PilgrimageRepository pilgrimageRepository;
    private final VisitedPilgrimageRepository visitedPilgrimageRepository;
    private final SortGuestBookByLatestStrategy sortGuestBookByLatestStrategy;
    private final SortGuestBookByLikedStrategy sortGuestBookByLikedStrategy;
    private final SearchGuestBookByTitle searchGuestBookByTitle;
    private final SearchGuestBookByNickname searchGuestBookByNickname;
    private final SearchGuestBookByContent searchGuestBookByContent;
    private final SecurityUtil securityUtil;

    /**
     * 당일 실시간 인기글 5개를 보여줌
     *
     * @return
     */
    public List<TrendingPostResponseDto.TrendingPostRank> getTodayTrendingGuestBook() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        List<GuestBook> guestBooks =
                guestBookImplRepository.findByCreatedAtBetweenOrderByLikeCountDesc(startOfDay, now, 5);
        if (guestBooks.isEmpty()) {
            throw new RestApiException(ErrorCode.GUESTBOOK_NOT_FOUND);
        }
        guestBooks.subList(0, Math.min(5, guestBooks.size()));

        List<TrendingPostResponseDto.TrendingPostRank> trendingPostsRank = new ArrayList<>();
        for (int i = 0; (i < guestBooks.size()) && (i < 5); i++) {
            trendingPostsRank.add(TrendingPostResponseDto.TrendingPostRank.of(guestBooks.get(i)));
        }
        return trendingPostsRank;
    }

    /**
     * sort에 따라 전체 정시순례 인증글들을 페이징해서 보여주는 기능
     *
     * @param page
     * @param size
     * @param sort
     * @return sort에 따라 전체 정시순례 인증글들
     */
    public List<GuestBookResponseDto.TotalGuestBookInfo> getTotalGuestBooksBySort(
            int page, int size, String sort
    ) {
        SortStrategy<GuestBook> sortStrategy;
        if ("latest".equals(sort)) {
            sortStrategy = sortGuestBookByLatestStrategy;
        } else if ("liked".equals(sort)) {
            sortStrategy = sortGuestBookByLikedStrategy;
        } else {
            throw new RestApiException(ErrorCode.SORT_TYPE_NOT_ALLOWED);
        }
        List<GuestBook> guestBooks = sortStrategy.sort(page, size);
        if (guestBooks.isEmpty()) {
            return Collections.emptyList();
        }
        return guestBooks.stream()
                .map(GuestBookConverter::toTotalGuestBookInfo)
                .toList();
    }

    /**
     * 내가 작성한 글
     *
     * @param page
     * @param size
     * @return
     */
    public List<GuestBookResponseDto.MyGuestBookInfo> getMyGuestBooks(int page, int size) {
        Member member = securityUtil.getUser();
        List<GuestBook> myGuestBooks =
                guestBookImplRepository.findAllByMemberIdOrderByCreatedAtDesc(member.getId(), page, size);
        if (myGuestBooks.isEmpty()) {
            return Collections.emptyList();
        }
        return myGuestBooks.stream()
                .map(GuestBookConverter::toGuestBook).toList();
    }

    /**
     * 성지 순례 인증글 상세 정보
     *
     * @param guestBookId
     * @param member
     * @return
     */
    public GuestBookResponseDto.DetailGuestBookDto getDetailGuestBookInfo(Long guestBookId, Member member) {
        GuestBook guestBook = guestBookImplRepository.findOneById(guestBookId);
        if (guestBook == null) {
            throw new RestApiException(ErrorCode.GUESTBOOK_NOT_FOUND);
        }
        Long completeNumber = getCompletePilgrimageCount(member, guestBook.getPilgrimage().getRally().getId());
        GuestBookResponseDto.PilgrimageInfo pilgrimageInfo =
                GuestBookConverter.toPilgrimageInfo(guestBook.getPilgrimage(), completeNumber);
        if (member == null) {
            return GuestBookConverter.toDetailGuestBookInfo(guestBook, false, false, pilgrimageInfo);
        }
        return GuestBookConverter.toDetailGuestBookInfo(guestBook, isLiked(guestBook.getId(), member.getId()),
                isWriter(guestBook, member.getId()), pilgrimageInfo);
    }

    /**
     * 사용자가 해당 랠리에서 몇개의 성지순례를 완료했는지 알려주는 함수
     *
     * @param member
     * @param rallyId
     * @return
     */
    public long getCompletePilgrimageCount(Member member, Long rallyId) {
        if (member == null) {
            return 0L;
        }
        Long memberId = member.getId();
        List<Long> pilgrimageIds =
                pilgrimageRepository.findByRallyId(rallyId).stream().map(Pilgrimage::getId).toList();
        return visitedPilgrimageRepository.countByMemberIdAndPilgrimageIdIn(memberId, pilgrimageIds);
    }


    /**
     * searchTypeㅈ(제목, 닉네임, 내용)을 기반으로 성지순레 인증글을 가져오는 함 (게시글 생성일의 내림차순으로 정렬)
     *
     * @param page
     * @param size
     * @param searchType
     * @param keyword
     * @return
     */
    public List<GuestBookResponseDto.TotalGuestBookInfo> getTotalPostByKeyword(
            int page, int size, String searchType, String keyword
    ) {
        SearchStrategy<GuestBook> searchStrategy;
        if ("title".equals(searchType)) {
            searchStrategy = searchGuestBookByTitle;
        } else if ("nickname".equals(searchType)) {
            searchStrategy = searchGuestBookByNickname;
        } else if ("content".equals(searchType)) {
            searchStrategy = searchGuestBookByContent;
        } else {
            throw new RestApiException(ErrorCode.SEARCH_TYPE_NOT_ALLOWED);
        }
        if (keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<GuestBook> guestBooks = searchStrategy.search(keyword, page, size);
        if (guestBooks.isEmpty()) {
            return Collections.emptyList();
        }
        return guestBooks.stream()
                .map(GuestBookConverter::toTotalGuestBookInfo).toList();
    }

    /**
     * 해당 글에 좋아요를 눌렀는지 확인
     *
     * @param guestBookId
     * @param memberId
     * @return
     */
    private Boolean isLiked(Long guestBookId, Long memberId) {
        return likedPostRepository.existsByGuestBookIdAndMemberId(guestBookId, memberId);
    }

    /**
     * 해당 글의 작성자인지 확인
     *
     * @param guestBook
     * @param memberId
     * @return
     */
    private Boolean isWriter(GuestBook guestBook, Long memberId) {
        return guestBook.getMember().getId().equals(memberId);
    }

}
