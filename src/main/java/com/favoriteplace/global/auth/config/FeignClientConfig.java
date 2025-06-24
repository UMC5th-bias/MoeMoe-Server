package com.favoriteplace.global.auth.config;


import com.favoriteplace.global.exception.RestApiException;
import feign.FeignException;
import feign.RequestInterceptor;
import feign.Response;
import feign.codec.ErrorDecoder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static com.favoriteplace.global.exception.ErrorCode.NOT_FOUND;
import static com.favoriteplace.global.exception.ErrorCode.TOKEN_NOT_VALID;


@Configuration
@Slf4j
public class FeignClientConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

    @Bean
    public RequestInterceptor transactionLoggingInterceptor() {
        return requestTemplate -> {
            boolean isTransactionActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Feign 요청 시 트랜잭션 활성 여부: {}", isTransactionActive);
        };
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