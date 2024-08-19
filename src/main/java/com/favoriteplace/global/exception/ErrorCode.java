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
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, 2002, "로그인에 실패했습니다."),
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, 2003, "해당 이메일로 가입한 유저가 존재합니다."),
    NOT_VAILD_EMAIL_AUTHCODE(HttpStatus.BAD_REQUEST, 2004, "이메일 인증번호가 일치하지 않습니다."),
    USER_NOT_AUTHOR(HttpStatus.FORBIDDEN, 2005, "해당 게시글의 작성자가 아닙니다."),
    CANT_BLOCK_SELF(HttpStatus.FORBIDDEN, 2006, "스스로를 차단할 수 없습니다."),
    TOKEN_NOT_VALID(HttpStatus.BAD_REQUEST, 2007, "not valid token"),

    //Pilgrimage (3000번대)
    PILGRIMAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, 3001, "성지순례 정보를 찾을 수 없습니다."),
    PILGRIMAGE_ALREADY_CERTIFIED(HttpStatus.BAD_REQUEST, 3002, "인증 후 24시간이 지나야 재인증할 수 있습니다."),
    PILGRIMAGE_CAN_NOT_CERTIFIED(HttpStatus.BAD_REQUEST, 3003, "인증 장소와 현재 위치의 거리가 너무 멉니다."),
    PILGRIMAGE_NOT_CERTIFIED(HttpStatus.BAD_REQUEST, 3004, "인증글은 인증 후 작성할 수 있습니다."),

    //Rally (4000번대)
    RALLY_NOT_FOUND(HttpStatus.BAD_REQUEST, 4001, "랠리 정보를 찾을 수 없습니다."),
    TRENDING_RALLY_NOT_FOUND(HttpStatus.BAD_REQUEST, 4002, "이달의 추천 랠가 존재하지 않습니다."),

    //Post (5000번대)
    POST_NOT_FOUND(HttpStatus.BAD_REQUEST, 5001, "자유게시판 게시글이 존재하지 않습니다."),

    //GuestBook (6000번대)
    GUESTBOOK_NOT_FOUND(HttpStatus.BAD_REQUEST, 6001, "성지순례 인증글이 존재하지 않습니다."),
    GUESTBOOK_MUST_INCLUDE_IMAGES(HttpStatus.BAD_REQUEST, 6002, "반드시 이미지를 등록해야 합니다."),

    //커뮤니티 (7000번대)
    SORT_TYPE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, 7001, "존재하지 않는 정렬 방식입니다."),
    SEARCH_TYPE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, 7002, "존재하지 않는 검색 방식입니다."),

    // 지역 (8000번대)
    ADDRESS_NOT_FOUND(HttpStatus.BAD_REQUEST, 8001, "존재하지 않는 지역입니다."),

    // 아이템 (9000번대)
    ITEM_TYPE_NOT_FOUND(HttpStatus.BAD_REQUEST, 9001, "존재하지 않는 아이템 타입입니다."),
    ITEM_NOT_EXISTS(HttpStatus.BAD_REQUEST, 9002, "존재하지 않는 아이템입니다."),
    ITEM_NOT_ACQUIRED(HttpStatus.BAD_REQUEST, 9003, "보유하지 않은 아이템입니다."),

    //댓글 (10000번대)
    COMMENT_NOT_FOUND(HttpStatus.BAD_REQUEST, 10001, "댓글이 존재하지 않습니다."),
    COMMENT_NOT_PARENT(HttpStatus.BAD_REQUEST, 10002, "해당 댓글은 최상위 댓글이 아닙니다."),
    COMMENT_NOT_CHILD(HttpStatus.BAD_REQUEST, 10003, "해당 댓글은 대댓글이 아닙니다."),
    COMMENT_ALREADY_DELETED(HttpStatus.BAD_REQUEST, 10004, "해당 댓글은 삭제되어 존재하지 않습니다."),

    //이미지 (11000번대)
    IMAGE_FORMAT_ERROR(HttpStatus.BAD_REQUEST, 11001, "올바른 이미지 파일이 아닙니다."),
    IMAGE_NOT_READABLE(HttpStatus.BAD_REQUEST, 11002, "이미지 파일을 읽을 수 없습니다."),
    IMAGE_CANNOT_UPLOAD(HttpStatus.BAD_REQUEST, 11003, "이미지 파일을 업로드할 수 없습니다."),
    IMAGE_SIZE_TOO_BIG(HttpStatus.PAYLOAD_TOO_LARGE, 11004, "각각의 이미지 파일의 사이즈가 4MB를 넘어갈 수 없습니다."),

    //알림 (12000번대)
    TOKEN_ALARM_NOT_SEND(HttpStatus.BAD_REQUEST, 120001, "[token] push 알림이 전송되지 않았습니다."),
    TOPIC_ALARM_NOT_SEND(HttpStatus.BAD_REQUEST, 120002, "[topic] push 알림이 전송되지 않았습니다."),
    TOPIC_SUBSCRIBE_FAIL(HttpStatus.BAD_REQUEST, 12003, "[topic] 구독에 실패했습니다."),
    TOPIC_UNSUBSCRIBE_FAIL(HttpStatus.BAD_REQUEST, 12004, "[topic] 구독 취소에 실패했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

}
