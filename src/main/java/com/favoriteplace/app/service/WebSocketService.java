package com.favoriteplace.app.service;

import com.favoriteplace.app.domain.travel.Pilgrimage;
import com.favoriteplace.app.dto.travel.PilgrimageSocketDto;
import com.favoriteplace.app.repository.PilgrimageRepository;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.security.CustomUserDetails;
import com.google.api.gax.rpc.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;
    private final PilgrimageCommandService pilgrimageService;
    private final PilgrimageRepository pilgrimageRepository;
    private Map<Long, PilgrimageSocketDto.ButtonState> lastButtonStateCache = new ConcurrentHashMap<>();

    public void handleLocationUpdate(Long pilgrimageId, double latitude, double longitude) {
        Pilgrimage pilgrimage = pilgrimageRepository.findById(pilgrimageId)
                .orElseThrow(()->new RestApiException(ErrorCode.PILGRIMAGE_NOT_FOUND));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        PilgrimageSocketDto.ButtonState newState = pilgrimageService.determineButtonState(userDetails.getMember(), pilgrimage);
        PilgrimageSocketDto.ButtonState lastState = lastButtonStateCache.get(userDetails.getMember().getId());

        if (lastState == null || !newState.equals(lastState)) {
            lastButtonStateCache.put(userDetails.getMember().getId(), newState);
            messagingTemplate.convertAndSend("/pub/statusUpdate/" + pilgrimageId, newState);
        }
    }
}
