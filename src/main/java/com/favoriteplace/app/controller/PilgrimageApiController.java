package com.favoriteplace.app.controller;

import com.favoriteplace.app.dto.travel.PilgrimageDto;
import com.favoriteplace.app.dto.travel.RallyDto;
import com.favoriteplace.app.service.PilgrimageQueryService;
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

    // 성지순례 랠리 상세
    // 회원 + 비회원
    // jwt 수정 필요함
    @GetMapping("/{rallyId}")
    public RallyDto.RallyDetailResponseDto getRallyDetail(@PathVariable("rallyId")Long rallyId){
        RallyDto.RallyDetailResponseDto dto = pilgrimageQueryService.getRallyDetail(rallyId);
        return dto;
    }

    // 성지순례 랠리 장소리스트
    // 회원 + 비회원
    // jwt 수정 필요함
    @GetMapping("/{rallyId}/list")
    public RallyDto.RallyAddressListDto getRallyAddressList(@PathVariable("rallyId")Long rallyId){
        RallyDto.RallyAddressListDto dto = pilgrimageQueryService.getRallyAddressList(rallyId);
        return dto;
    }

    // 성지순례 랠리 장소 상세
    // 회원
    // jwt 수정 필요함
    @GetMapping("/detail/{pilgrimageId}")
    public PilgrimageDto.PilgrimageDetailDto getPilgrimageDetail(@PathVariable("pilgrimageId")Long pilgrimageId){
        PilgrimageDto.PilgrimageDetailDto dto = pilgrimageQueryService.getPilgrimageDetail(pilgrimageId);
        return dto;
    }
}
