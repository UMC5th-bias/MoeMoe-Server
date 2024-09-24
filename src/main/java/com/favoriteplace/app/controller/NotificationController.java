package com.favoriteplace.app.controller;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.dto.NotificationResponseDto;
import com.favoriteplace.app.service.NotificationService;
import com.favoriteplace.global.util.SecurityUtil;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Validated
public class NotificationController {
    private final NotificationService notificationService;
    private final SecurityUtil securityUtil;

    // 알림 전체 조회
    @GetMapping()
    public ResponseEntity<NotificationResponseDto> getAllNotification(
            @Min(value = 1, message = "page는 1 이상입니다.") @RequestParam(required = false, defaultValue = "1") Integer page,
            @Min(value = 1, message = "size는 1 이상입니다.") @RequestParam(required = false, defaultValue = "1") Integer size
    ){
        Member member = securityUtil.getUser();
        NotificationResponseDto response = notificationService.getAllNotification(member, page, size);
        return ResponseEntity.ok(response);
    }

    // 알림 한번에 다 읽음 처리
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204")
    })
    @PatchMapping()
    public ResponseEntity<Void> readAllNotification(){
        Member member = securityUtil.getUser();
        notificationService.readAllNotification(member);
        return ResponseEntity.noContent().build();
    }

    // 특정 알림 읽음 처리
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204")
    })
    @PatchMapping("/{notificationId}")
    public ResponseEntity<Void> readNotification(
            @PathVariable Long notificationId
    ){
        Member member = securityUtil.getUser();
        notificationService.readNotification(notificationId, member);
        return ResponseEntity.noContent().build();
    }

    // 특정 알림 삭제
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204")
    })
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long notificationId
    ){
        Member member = securityUtil.getUser();
        notificationService.deleteNotification(notificationId, member);
        return ResponseEntity.noContent().build();
    }

}
