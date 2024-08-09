package com.favoriteplace.app.controller;

import com.favoriteplace.app.service.PilgrimageCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@Controller
@Slf4j
@RequiredArgsConstructor
public class PilgrimageSocketController {
    private final PilgrimageCommandService pilgrimageService;

    @MessageMapping("/location")
    @SendTo("/pilgrimage")
    public String pilgrimageCertify(String testMsg){
        log.info("socket message: " + testMsg);
        return testMsg;
    }
}
