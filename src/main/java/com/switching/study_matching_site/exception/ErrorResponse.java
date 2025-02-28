package com.switching.study_matching_site.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private String message;  // 에러 메시지
    private int status;  // HTTP 상태 코드
    private List<FieldError> errors;  // 필드 오류 리스트
    private String code;  // 에러 코드

    // 기본적인 ErrorResponse 생성
    private ErrorResponse(final ErrorCode errorCode, final List<FieldError> errors) {
        this.message = errorCode.getMessage();  // 기본적으로 ErrorCode에서 메시지를 가져옴
        this.status = errorCode.getStatus();
        this.code = errorCode.getCode();
        this.errors = errors != null ? errors : new ArrayList<>();  // 빈 배열 보장
    }

    private ErrorResponse(final ErrorCode errorCode) {
        this(errorCode, new ArrayList<>());
    }

    // 예외 응답을 생성하는 정적 메서드 (FieldError 없음)
    public static ErrorResponse of(final ErrorCode errorCode) {
        return new ErrorResponse(errorCode);
    }

    // 예외 응답을 생성하는 정적 메서드 (FieldError 있음)
    public static ErrorResponse of(final ErrorCode errorCode, BindingResult bindingResult) {
        List<FieldError> fieldErrors = FieldError.of(bindingResult);
        return new ErrorResponse(errorCode, fieldErrors);
    }

    public void setErrors(List<FieldError> errors) {
        this.errors = errors;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FieldError {
        private String field;
        private String value;
        private String reason;

        FieldError(String field, String value, String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        // `BindingResult`에서 `FieldError` 리스트 추출하여 변환하는 메서드
        public static List<FieldError> of(final BindingResult bindingResult) {
            return bindingResult.getFieldErrors().stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() != null ? error.getRejectedValue().toString() : null,
                            error.getDefaultMessage()
                    ))
                    .collect(Collectors.toList());
        }
    }
}
