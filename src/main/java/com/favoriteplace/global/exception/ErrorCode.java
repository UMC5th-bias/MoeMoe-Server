package com.favoriteplace.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    /**
     * 에러코드 자유롭게 추가
     */
    INVALID_ARGUMENT_ERROR(HttpStatus.BAD_REQUEST, 400, "올바르지 않은 파라미터입니다."),
    INVALID_FORMAT_ERROR(HttpStatus.BAD_REQUEST,400, "올바르지 않은 포맷입니다."),
    INVALID_TYPE_ERROR(HttpStatus.BAD_REQUEST, 400, "올바르지 않은 타입입니다."),
    ILLEGAL_ARGUMENT_ERROR(HttpStatus.BAD_REQUEST, 400, "필수 파라미터가 없습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    INVALID_HTTP_METHOD(HttpStatus.METHOD_NOT_ALLOWED, 400, "잘못된 Http Method 요청입니다."),

    //User (2000번대)
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, 2001, "유저를 찾을 수 없습니다."),

    //Pilgrimage (3000번대)
    PILGRIMAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, 3001, "성지순례 정보를 찾을 수 없습니다."),

    //Rally (4000번대)
    RALLY_NOT_FOUND(HttpStatus.BAD_REQUEST, 4001, "랠리 정보를 찾을 수 없습니다."),

    //Post (5000번대)
    POST_NOT_FOUND(HttpStatus.BAD_REQUEST, 5001, "자유게시판 게시글이 존재하지 않습니다.");


    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

}
