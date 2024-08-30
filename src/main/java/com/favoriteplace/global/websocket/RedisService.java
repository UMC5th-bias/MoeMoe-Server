package com.favoriteplace.global.websocket;

import com.favoriteplace.app.domain.Member;
import com.favoriteplace.app.domain.travel.Pilgrimage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class RedisService {
    @Qualifier("customRedisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CERTIFICATION_KEY_PREFIX = "certification:";
    private static final Duration CERTIFICATION_EXPIRATION = Duration.ofMinutes(1);

    // 사용자가 인증 장소에 접속한 시점을 저장
    public void saveCertificationTime(Long userId, Long pilgrimageId) {
        String key = CERTIFICATION_KEY_PREFIX + userId + ":" + pilgrimageId;
        String now = DateTimeFormatter.ISO_INSTANT.format(Instant.now());
        redisTemplate.opsForValue().set(key, now);
        redisTemplate.expire(key, CERTIFICATION_EXPIRATION);
    }

    // 인증 시점에서 1분이 지났는지 확인
    public boolean isCertificationExpired(Member member, Pilgrimage pilgrimage) {
        String key = CERTIFICATION_KEY_PREFIX + member.getId() + ":" + pilgrimage.getId();
        String savedTimeString = (String) redisTemplate.opsForValue().get(key);

        if (savedTimeString == null) {
            return true;
        }

        Instant savedTime = Instant.parse(savedTimeString);
        return savedTime.isBefore(Instant.now().minus(CERTIFICATION_EXPIRATION));
    }

    public void deleteCertificationTime(Long userId, Long pilgrimageId) {
        String key = CERTIFICATION_KEY_PREFIX + userId + ":" + pilgrimageId;
        redisTemplate.delete(key);
    }
}