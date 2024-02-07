package com.favoriteplace.global.exception;

import static com.favoriteplace.global.exception.ErrorCode.IMAGE_SIZE_TOO_BIG;
import static com.favoriteplace.global.exception.ErrorCode.INVALID_HTTP_METHOD;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerAdvice {

    //모든 에러 -> 하위 에러에서 못받을 때
    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception e){
        // NestedExceptionUtils.getMostSpecificCause() -> 가장 구체적인 원인, 즉 가장 근본 원인을 찾아서 반환
        log.error("[Exception] cause: {} , message: {}", NestedExceptionUtils.getMostSpecificCause(e), e.getMessage());
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getHttpStatus(), errorCode.getCode(), errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(RestApiException.class)
    public ResponseEntity handleSystemException(RestApiException e){
        log.error("[SystemException] cause: {}, message: {}",NestedExceptionUtils.getMostSpecificCause(e),e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getHttpStatus(),errorCode.getCode(),  errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }

    //메소드가 잘못되었거나 부적합한 인수를 전달했을 경우 -> 필수 파라미터 없을 때
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleIllegalArgumentException(IllegalArgumentException e){
        log.error("[IlleagalArgumentException] cause: {} , message: {}",NestedExceptionUtils.getMostSpecificCause(e),e.getMessage());
        ErrorCode errorCode = ErrorCode.ILLEGAL_ARGUMENT_ERROR;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getHttpStatus(),errorCode.getCode(),
            String.format("%s %s", errorCode.getMessage(), NestedExceptionUtils.getMostSpecificCause(e).getMessage()));
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }

    //@Valid 유효성 검사에서 예외가 발생했을 때 -> requestbody에 잘못 들어왔을 때
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error("[MethodArgumentNotValidException] cause: {}, message: {}",NestedExceptionUtils.getMostSpecificCause(e),e.getMessage());
        ErrorCode errorCode = ErrorCode.INVALID_ARGUMENT_ERROR;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getHttpStatus(),
            errorCode.getCode(),
            errorCode.getMessage(),
            e.getBindingResult());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }

    //잘못된 포맷 요청 -> Json으로 안보내다던지
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity handleHttpMessageNotReadableException(HttpMessageNotReadableException e){
        log.error("[HttpMessageNotReadableException] cause: {}, message: {}",NestedExceptionUtils.getMostSpecificCause(e),e.getMessage());
        ErrorCode errorCode = ErrorCode.INVALID_FORMAT_ERROR;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getHttpStatus(), errorCode.getCode(),  errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMethodException(
        HttpRequestMethodNotSupportedException e,
        HttpServletRequest request
    ) {
        log.error("[HttpRequestMethodNotSupportedException] " +
                "url: {} | errorType: {} | errorMessage: {} | cause Exception: ",
            request.getRequestURL(), INVALID_HTTP_METHOD, INVALID_HTTP_METHOD.getMessage(), e);

        ErrorCode errorCode = INVALID_HTTP_METHOD;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getHttpStatus(), errorCode.getCode(),  errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
    }

    //이미지 사이즈 초과했을 때
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity handleMaxUploadSizeException(MaxUploadSizeExceededException e) {
        log.error("[MaxUploadSizeExceededException (파일 업로드 크기 초과 (각 파일 4MB로 제한)] message : {}" , e.getMessage());
        ErrorCode errorCode = IMAGE_SIZE_TOO_BIG;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getHttpStatus(), errorCode.getCode(), errorCode.getMessage());
        return ResponseEntity.status(errorResponse.getHttpStatus()).body(errorResponse.getMessage());
    }
}