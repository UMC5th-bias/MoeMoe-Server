package com.favoriteplace.app.member.service;

import com.favoriteplace.app.member.domain.Member;
import com.favoriteplace.global.auth.provider.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TokenService {
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void updateRefreshToken(Member member, String refreshToken) {
        member.updateRefreshToken(refreshToken);
    }

    @Transactional
    public void logoutAndInvalidateToken(Member member, Long expiration, String accessToken) {
        deleteRefreshToken(member);
        blacklistAccessToken(expiration, accessToken);
    }

    private void deleteRefreshToken (Member member) {
        if (member.getRefreshToken() != null && !member.getRefreshToken().isEmpty()) {
            member.deleteRefreshToken(member.getRefreshToken());
        }
    }

    private void blacklistAccessToken(Long expiration, String accessToken) {
        redisTemplate.opsForValue()
                .set(accessToken, "logout", expiration, TimeUnit.MICROSECONDS);
    }

}
