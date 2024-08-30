package com.favoriteplace.app.controller;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.travel.Pilgrimage;
import com.favoriteplace.app.dto.travel.PilgrimageDto;
import com.favoriteplace.app.dto.travel.PilgrimageSocketDto;
import com.favoriteplace.app.repository.PilgrimageRepository;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PilgrimageSocketController {
    private final PilgrimageCommandService pilgrimageService;
    private final PilgrimageRepository pilgrimageRepository;
    private Map<Long, Map<Long, PilgrimageSocketDto.ButtonState>> lastButtonStateCache = new ConcurrentHashMap<>();

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
    public PilgrimageSocketDto.ButtonState checkUserLocation(@DestinationVariable Long pilgrimageId, Principal principal, PilgrimageDto.PilgrimageCertifyRequestDto userLocation) {
        if (principal == null)
            throw new RestApiException(ErrorCode.USER_NOT_AUTHOR);

        CustomUserDetails userDetails = (CustomUserDetails)
                ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        Member member = userDetails.getMember();

        Pilgrimage pilgrimage = pilgrimageRepository.findById(pilgrimageId)
                .orElseThrow(() -> new RestApiException(ErrorCode.PILGRIMAGE_NOT_FOUND));

        // 위치 정보 바탕으로 인증 가능 여부 Redis 저장
        pilgrimageService.isLocationVerified(member, pilgrimage,
                userLocation.getLatitude(), userLocation.getLongitude());

        // 버튼 상태 업데이트
        PilgrimageSocketDto.ButtonState buttonState =
                pilgrimageService.determineButtonState(member, pilgrimage);

        // 이전 버튼 상태와 비교해서 달라졌다면 전송, 아니면 null
        synchronized (this) {
            lastButtonStateCache.putIfAbsent(member.getId(), new ConcurrentHashMap<>());
            Map<Long, PilgrimageSocketDto.ButtonState> pilgrimageStateMap = lastButtonStateCache.get(member.getId());

            if (pilgrimageStateMap == null) {
                pilgrimageStateMap = new ConcurrentHashMap<>();
                lastButtonStateCache.put(member.getId(), pilgrimageStateMap);
            }

            PilgrimageSocketDto.ButtonState lastState = pilgrimageStateMap.get(pilgrimageId);

            log.debug("Last state: {}", lastState);
            log.debug("Current state: {}", buttonState);

            if (lastState == null || !buttonState.equals(lastState)) {
                pilgrimageStateMap.put(pilgrimageId, buttonState);
                return buttonState;
            }
        }
        return null;
    }

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

        Pilgrimage pilgrimage = pilgrimageRepository.findById(pilgrimageId)
                .orElseThrow(()->new RestApiException(ErrorCode.PILGRIMAGE_NOT_FOUND));

        PilgrimageSocketDto.ButtonState buttonState = pilgrimageService.determineButtonState(member, pilgrimage);
        lastButtonStateCache.get(member.getId()).put(pilgrimageId, buttonState);
        return buttonState;
    }
}
