package com.favoriteplace.app.controller;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.dto.CommonResponseDto;
import com.favoriteplace.app.dto.travel.PilgrimageCertifyRequestDto;
import com.favoriteplace.app.dto.travel.PilgrimageResponseDto;
import com.favoriteplace.app.dto.travel.PilgrimageSocketDto;
import com.favoriteplace.app.service.PilgrimageCommandService;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PilgrimageSocketController {
    private final PilgrimageCommandService pilgrimageService;

    /**
     * 최초 접근 시 버튼 상태 전달하는 컨트롤러
     *
     * 요청 컨트롤러 /app/connect/{pilgrimageId}
     * 응답 컨트롤러 /pub/statusUpdate/{pilgrimageId}
     *
     * @param pilgrimageId 성지순례 ID
     * @return
     */
    @MessageMapping("/connect/{pilgrimageId}")
    @SendTo("/pub/statusUpdate/{pilgrimageId}")
    public PilgrimageSocketDto.ButtonState sendInitialStatus(@DestinationVariable Long pilgrimageId,  Principal principal) {
        if (principal == null)
            throw new RestApiException(ErrorCode.USER_NOT_AUTHOR);

        CustomUserDetails userDetails = (CustomUserDetails)
                ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        Member member = userDetails.getMember();

        PilgrimageSocketDto.ButtonState buttonState = pilgrimageService.initButton(member, pilgrimageId);
        return buttonState;
    }

    /**
     * 위도/경도 전달 시 상태 변경 알리는 컨트롤러
     *
     * 요청 컨트롤러 /app/location/{pilgrimageId}
     * 응답 컨트롤러  /pub/statusUpdate/{pilgrimageId}
     *
     * @param pilgrimageId 성지순례 ID
     * @param userLocation 위도/경도
     * @return 버튼 상태 json
     */
    @MessageMapping("/location/{pilgrimageId}")
    @SendTo("/pub/statusUpdate/{pilgrimageId}")
    public PilgrimageSocketDto.ButtonState checkUserLocation(
            @DestinationVariable Long pilgrimageId,
            Principal principal,
            PilgrimageCertifyRequestDto userLocation
    ) {
        if (principal == null)
            throw new RestApiException(ErrorCode.USER_NOT_AUTHOR);

        CustomUserDetails userDetails = (CustomUserDetails)
                ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        Member member = userDetails.getMember();

        return pilgrimageService.buttonStatusUpdate(pilgrimageId, userLocation, member);
    }

    /**
     * 성지순례 인증하기 버튼 클릭 이벤트
     *
     * 요청 컨트롤러 /app/certify/{pilgrimageId}
     * 응답 컨트롤러 /pub/cerfity/{pilgrimageId}
     *
     * @param pilgrimageId 성지순례 ID
     * @return
     */
    @MessageMapping("/certify/{pilgrimageId}")
    @SendTo("/pub/certify/{pilgrimageId}")
    public CommonResponseDto.RallyResponseDto certifyPilgrimage(
            @DestinationVariable Long pilgrimageId, Principal principal
    ) {
        if (principal == null)
            throw new RestApiException(ErrorCode.USER_NOT_AUTHOR);

        CustomUserDetails userDetails = (CustomUserDetails)
                ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        Member member = userDetails.getMember();

        return pilgrimageService.certifyToPilgrimage(pilgrimageId, member);
    }
}
