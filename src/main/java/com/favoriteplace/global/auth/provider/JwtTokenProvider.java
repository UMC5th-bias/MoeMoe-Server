package com.favoriteplace.global.auth.provider;

import com.favoriteplace.app.member.controller.dto.TokenInfoDto;
import com.favoriteplace.global.exception.RestApiException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import static com.favoriteplace.global.exception.ErrorCode.INVALID_JWT;
import static com.favoriteplace.global.exception.ErrorCode.EXPIRED_JWT;
import static com.favoriteplace.global.exception.ErrorCode.INVALID_SIGNATURE;
import static com.favoriteplace.global.exception.ErrorCode.UNSUPPORTED_JWT;

import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final Long ACCESS_TOKEN_EXPIRATION_TIME = 60 * 60 * 1000L;
    private static final Long REFRESH_TOKEN_EXPIRATION_TIME = 24 * 60 * 60 * 1000L * 14;

    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String key;

    public String issueRefreshToken(String userEmail) {
        return issueToken(userEmail, REFRESH_TOKEN_EXPIRATION_TIME);
    }

    public String issueAccessToken(String userEmail) {
        return issueToken(userEmail, ACCESS_TOKEN_EXPIRATION_TIME);
    }

    public Authentication getAuthentication(String token) {
        String userEmail = parseClaims(token).getSubject();

        UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
        return new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword());
    }

    public Long getExpiration(String token) {
        Date expiration = parseClaims(token).getExpiration();
        long now = new Date().getTime();

        return (expiration.getTime() - now);
    }

    public void validateToken(String token) {
        try {
            parseClaims(token);
        } catch (MalformedJwtException e) {
            throw new RestApiException(INVALID_JWT);
        } catch (ExpiredJwtException e) {
            throw new RestApiException(EXPIRED_JWT);
        } catch (UnsupportedJwtException e) {
            throw new RestApiException(UNSUPPORTED_JWT);
        } catch (SignatureException e) {
            throw new RestApiException(INVALID_SIGNATURE);
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    private String issueToken(String userEmail, Long expirePeriod) {
        Claims claims = Jwts.claims().setSubject(userEmail);
        Date now = new Date();

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirePeriod))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();

        return token;
    }

}
