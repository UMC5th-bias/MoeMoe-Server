package com.favoriteplace.app.controller;

import com.favoriteplace.app.dto.travel.PilgrimageDto;
import com.favoriteplace.app.service.PilgrimageCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PilgrimageSocketController {
    private final PilgrimageCommandService pilgrimageService;

    /**
     * 테스트 컨트롤러
     * 요청 /app/test
     * 응답 /pub/pilgrimage
     * @param testMsg
     * @return
     */
    @MessageMapping("/test")
    @SendTo("/pub/pilgrimage")
    public String pilgrimageCertify(String testMsg){
        log.info("socket message: " + testMsg);
        return testMsg;
    }

    /**
     * 위도/경도 전달 시 상태 변경 알리는 컨트롤러
     * 요청 컨트롤러 /app/location/{pilgrimageId}
     * 응답 컨트롤러  /pub/statusUpdate/{pilgrimageId}
     * @param pilgrimageId 성지순례 ID
     * @param userLocation 위도/경도
     * @return
     */
    @MessageMapping("/location/{pilgrimageId}")
    @SendTo("/pub/statusUpdate/{pilgrimageId}")
    public Boolean checkUserLocation(@DestinationVariable Long pilgrimageId, PilgrimageDto.PilgrimageCertifyRequestDto userLocation) {
        log.info("socket pilgrimage: "+pilgrimageId);
        boolean isUserAtPilgrimage = pilgrimageService.isUserAtPilgrimage(pilgrimageId, userLocation.getLatitude(), userLocation.getLongitude());
        if (!isUserAtPilgrimage) {
            // 여기에 이벤트 동작 추가
            return false;
        }
        return true;
    }

    /**
     * 최초 접근 시 버튼 상태 전달하는 컨트롤러
     * 요청 컨트롤러 /app/connect/{pilgrimageId}
     * 응답 컨트롤러 /pub/statusUpdate/{pilgrimageId}
     * @param pilgrimageId
     * @return
     */
    @MessageMapping("/connect/{pilgrimageId}")
    @SendTo("/pub/statusUpdate/{pilgrimageId}")
    public Boolean sendInitialStatus(@DestinationVariable Long pilgrimageId) {
        log.info("User connected to pilgrimage: " + pilgrimageId);
        return true; // 초기 버튼 상태
    }
}
