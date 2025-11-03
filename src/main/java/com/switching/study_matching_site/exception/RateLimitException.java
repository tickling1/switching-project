package com.switching.study_matching_site.exception;

public class RateLimitException extends BusinessException {
    public RateLimitException(ErrorCode errorCode) {
        super(errorCode);
    }
}
