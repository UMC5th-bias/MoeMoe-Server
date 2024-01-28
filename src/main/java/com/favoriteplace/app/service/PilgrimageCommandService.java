package com.favoriteplace.app.service;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.travel.LikedRally;
import com.favoriteplace.app.domain.travel.Rally;
import com.favoriteplace.app.dto.CommonResponseDto;
import com.favoriteplace.app.repository.LikedRallyRepository;
import com.favoriteplace.app.repository.RallyRepository;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PilgrimageCommandService {
    private final RallyRepository rallyRepository;
    private final LikedRallyRepository likedRallyRepository;
    /***
     * 랠리 찜하기
     * @param rallyId 랠리 아이디
     * @param member 찜한 사용자
     * @return
     */
    public CommonResponseDto.PostResponseDto likeToRally(Long rallyId, Member member) {
        Rally rally = rallyRepository.findById(rallyId).orElseThrow(
                ()->new RestApiException(ErrorCode.RALLY_NOT_FOUND));
        LikedRally likedRally = likedRallyRepository.findByRallyAndMember(rally, member);
        if (likedRally == null) {
            LikedRally newLikedRally = LikedRally.builder()
                    .rally(rally)
                    .member(member)
                    .build();
            likedRallyRepository.save(newLikedRally);
            return CommonResponseDto.PostResponseDto.builder()
                    .success(true)
                    .message("찜 목록에 추가됐습니다.")
                    .build();
        } else {
            likedRallyRepository.delete(likedRally);
            return CommonResponseDto.PostResponseDto.builder()
                    .success(true)
                    .message("찜을 취소했습니다.")
                    .build();
        }
    }

    /***
     * 성지순례 방문 인증하기
     * @param pilgrimageId 성지순례 아이디
     * @param member 인증한 사용자
     * @return
     */
    public CommonResponseDto.PostResponseDto certifyToPilgrimage(Long pilgrimageId, Member member) {
        return null;
    }

    /***
     * 성지순례 방문 인증글 작성하기
     * @param pilgrimageId 성지순례 아이디
     * @param member 인증한 사용자
     * @return
     */
    public CommonResponseDto.PostResponseDto postToPilgrimage(Long pilgrimageId, Member member) {
        return null;
    }
}
