package com.favoriteplace.global.websocket;

import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.app.member.repository.MemberRepository;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;
import com.favoriteplace.global.auth.CustomUserDetails;
import com.favoriteplace.global.auth.provider.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class JwtChannelInterceptor implements ChannelInterceptor {
    private final MemberRepository userUtilityService;
    private final JwtTokenProvider jwtProvider;

    /**
     * WebSocket 연결 전 JWT 검사하고
     * WebSocket 세션에 사용자 정보 저장하는 인터셉터
     *
     * @param message
     * @param channel
     * @return
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authorization = accessor.getNativeHeader("Authorization");

            if (authorization != null && !authorization.isEmpty()) {
                String bearerToken = authorization.get(0);
                String jwt = bearerToken.startsWith("Bearer ") ? bearerToken.substring(7) : bearerToken;

                try {
                    // JWT 토큰 검증
                    if (!jwtProvider.validateToken(jwt)) {
                        throw new RestApiException(ErrorCode.USER_NOT_AUTHOR);
                    }

                    Authentication authentication = jwtProvider.getAuthentication(jwt);

                    Member member = userUtilityService.findByEmail(authentication.getName())
                            .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));

                    CustomUserDetails userDetails = new CustomUserDetails(member);
                    UsernamePasswordAuthenticationToken userInfo =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                    // WebSocket 세션에 사용자 정보 저장
                    accessor.setUser(userInfo);

                } catch (Exception e) {
                    log.error("JWT Verification Failed: " + e.getMessage());
                    return null;
                }
            } else {
                log.error("Authorization header is not found");
                return null;
            }
        }
        return message;
    }
}
