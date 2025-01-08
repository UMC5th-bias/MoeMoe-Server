package com.favoriteplace.global.auth.provider;

import com.favoriteplace.app.dto.member.MemberDto.TokenInfo;
import com.favoriteplace.global.exception.ErrorCode;
import com.favoriteplace.global.exception.RestApiException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

import java.util.Date;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String key;
    private final Long accessExpirePeriod = 24 * 60 * 60 * 1000L * 30;
    private final Long refreshExpirePeriod = 24 * 60 * 60 * 1000L * 40;
    private final UserDetailsService userDetailsService;

    public TokenInfo generateToken(String userEmail) {
        String accessToken = issueToken(userEmail, accessExpirePeriod);
        String refreshToken = issueToken(userEmail, refreshExpirePeriod);

        return TokenInfo.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String issueToken(String userEmail, Long expirePeriod) {
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

    // JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        String userPrincipal = Jwts.parser().
                setSigningKey(key)
                .parseClaimsJws(accessToken)
                .getBody().getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(userPrincipal);

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public Long getExpiration(String accessToken) {
        Date expiration = Jwts.parserBuilder().setSigningKey(key)
                .build().parseClaimsJws(accessToken).getBody().getExpiration();

        long now = new Date().getTime();

        return (expiration.getTime() - now);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException | MalformedJwtException e) {
            throw new RestApiException(ErrorCode.JWT_INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new RestApiException(ErrorCode.JWT_EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new RestApiException(ErrorCode.JWT_UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new RestApiException(ErrorCode.JWT_EMPTY_CLAIMS);
        }
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
