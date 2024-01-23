package com.favoriteplace.app.controller;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.dto.travel.PilgrimageDto;
import com.favoriteplace.app.dto.travel.RallyDto;
import com.favoriteplace.app.service.PilgrimageQueryService;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "/pilgrimage")
@RequiredArgsConstructor
public class PilgrimageApiController {
    private final PilgrimageQueryService pilgrimageQueryService;
    private final SecurityUtil securityUtil;

    // 내 성지순례 + 인증글 모아보기(메인)
    // 회원 + 비회원
    @GetMapping("")
    public PilgrimageDto.MyPilgrimageDto getMyPilgrimageDto(HttpServletRequest request) {
        Member member = securityUtil.getUserFromHeader(request);
        return pilgrimageQueryService.getMyPilgrimageDto(member);
    }

    // 이달의 추천 랠리 (메인)
    // 회원 + 비회원
    @GetMapping("/trending")
    public RallyDto.RallyTrendingDto getRallyTrending() {
        return pilgrimageQueryService.getRallyTrending();
    }

    // 성지순례 애니메이션 카테고리
    // 회원 + 비회원
    @GetMapping("/anime")
    public List<RallyDto.PilgrimageCategoryAnimeDto> getCategoryAnime(HttpServletRequest request){
        Member member = securityUtil.getUserFromHeader(request);
        return pilgrimageQueryService.getCategoryAnime(member);
    }

    // 성지순례 지역 카테고리
    @GetMapping("/region")
    public List<PilgrimageDto.PilgrimageCategoryRegionDto> getCategoryRegion(){
        return pilgrimageQueryService.getCategoryRegion();
    }

    // 성지순례 지역 상세 카테고리
    @GetMapping("/region/{regionId}")
    public PilgrimageDto.PilgrimageCategoryRegionDetailDto getCategoryRegionDetail(@PathVariable("regionId")Long regionId){
        return pilgrimageQueryService.getCategoryRegionDetail(regionId);
    }

    // 성지순례 랠리 상세
    // 랠리 찜 개발 후 테스트 필요
    // 회원 + 비회원
    @GetMapping("/{rallyId}")
    public RallyDto.RallyDetailResponseDto getRallyDetail(HttpServletRequest request,
                                                          @PathVariable("rallyId")Long rallyId){
        Member member = securityUtil.getUserFromHeader(request);
        return pilgrimageQueryService.getRallyDetail(rallyId, member);
    }

    // 성지순례 랠리 장소 리스트
    // 랠리 인증하기 개발 후 테스트 필요
    // 회원 + 비회원
    @GetMapping("/{rallyId}/list")
    public RallyDto.RallyAddressListDto getRallyAddressList(
            HttpServletRequest request, @PathVariable("rallyId")Long rallyId){
        Member member = securityUtil.getUserFromHeader(request);
        return pilgrimageQueryService.getRallyAddressList(rallyId, member);
    }

    // 성지순례 랠리 장소 상세
    // 회원
    @GetMapping("/detail/{pilgrimageId}")
        public PilgrimageDto.PilgrimageDetailDto getPilgrimageDetail(@PathVariable("pilgrimageId")Long pilgrimageId){
        // Jwt AuthenticationFilter에 엔드포인트 추가
        Member member = securityUtil.getUser();
        return pilgrimageQueryService.getPilgrimageDetail(
                pilgrimageId, member);
    }
}
