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

    @MessageMapping("/test")
    @SendTo("/pilgrimage")
    public String pilgrimageCertify(String testMsg){
        log.info("socket message: " + testMsg);
        return testMsg;
    }
    @MessageMapping("/location/{pilgrimageId}")
    @SendTo("/pub/statusUpdate/{pilgrimageId}")
    public Boolean checkUserLocation(@DestinationVariable Long pilgrimageId, PilgrimageDto.PilgrimageCertifyRequestDto userLocation) {
        log.info("socket pilgrimage: "+pilgrimageId);
        boolean isUserAtPilgrimage = pilgrimageService.isUserAtPilgrimage(pilgrimageId, userLocation.getLatitude(), userLocation.getLongitude());
        if (!isUserAtPilgrimage) {
            return null;
        }
        //
        return true;
    }
}
