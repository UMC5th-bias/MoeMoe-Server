package com.favoriteplace.global.auth.config;


import com.favoriteplace.global.exception.RestApiException;
import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.favoriteplace.global.exception.ErrorCode.NOT_FOUND;
import static com.favoriteplace.global.exception.ErrorCode.TOKEN_NOT_VALID;


@Configuration
public class FeignClientConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    public static class CustomErrorDecoder implements ErrorDecoder {

        @Override
        public Exception decode(String methodKey, Response response) {
            if (response.status() == 401) {
                // Unauthorized (401) 에러 처리
                return new RestApiException(TOKEN_NOT_VALID);
            } else if (response.status() == 404) {
                // Not Found (404) 에러 처리
                return new RestApiException(NOT_FOUND);
            }

            // 기본적으로는 FeignException을 던집니다.
            return FeignException.errorStatus(methodKey, response);
        }
    }
}