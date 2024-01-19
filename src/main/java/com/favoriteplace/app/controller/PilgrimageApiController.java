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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/pilgrimage")
@RequiredArgsConstructor
public class PilgrimageApiController {
    private final PilgrimageQueryService pilgrimageQueryService;
    private final SecurityUtil securityUtil;

    // 성지순례 랠리 상세
    // 랠리 찜 개발 후 테스트 필요
    // 회원 + 비회원
    @GetMapping("/{rallyId}")
    public RallyDto.RallyDetailResponseDto getRallyDetail(HttpServletRequest request, @PathVariable("rallyId")Long rallyId){
        Member member = securityUtil.getUserFromHeader(request);
        RallyDto.RallyDetailResponseDto dto = pilgrimageQueryService.getRallyDetail(rallyId, member);
        return dto;
    }

    // 성지순례 랠리 장소 리스트
    // 랠리 인증하기 개발 후 테스트 필요
    // 회원 + 비회원
    @GetMapping("/{rallyId}/list")
    public RallyDto.RallyAddressListDto getRallyAddressList(HttpServletRequest request, @PathVariable("rallyId")Long rallyId){
        Member member = securityUtil.getUserFromHeader(request);
        RallyDto.RallyAddressListDto dto = pilgrimageQueryService.getRallyAddressList(rallyId, member);
        return dto;
    }

    // 성지순례 랠리 장소 상세
    // 회원
    @GetMapping("/detail/{pilgrimageId}")
    public PilgrimageDto.PilgrimageDetailDto getPilgrimageDetail(@PathVariable("pilgrimageId")Long pilgrimageId){
        // Jwt AuthenticationFilter에 엔드포인트 추가
        PilgrimageDto.PilgrimageDetailDto dto = pilgrimageQueryService.getPilgrimageDetail(
                pilgrimageId, securityUtil.getUser());
        return dto;
    }
}
