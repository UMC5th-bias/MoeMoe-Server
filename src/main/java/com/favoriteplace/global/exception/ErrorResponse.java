package com.favoriteplace.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Getter
@RequiredArgsConstructor
@Builder
public class ErrorResponse {
    private final boolean success = false;
    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final List<ValidationError> errors;

    public static ErrorResponse of(HttpStatus httpStatus,int code, String message){
        return ErrorResponse.builder()
            .httpStatus(httpStatus)
            .code(code)
            .message(message)
            .build();
    }
    public static ErrorResponse of(HttpStatus httpStatus,int code, String message, BindingResult bindingResult){
        return ErrorResponse.builder()
            .httpStatus(httpStatus)
            .code(code)
            .message(message)
            .errors(ValidationError.of(bindingResult))
            .build();
    }

    @Getter
    public static class ValidationError{
        private final String field;
        private final String message;

        private ValidationError(FieldError fieldError){
            this.field = fieldError.getField();
            this.message = fieldError.getDefaultMessage();
        }

        public static List<ValidationError> of(final BindingResult bindingResult){
            return bindingResult.getFieldErrors().stream()
                .map(ValidationError::new)
                .toList();
        }

    }
}
