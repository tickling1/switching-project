package com.switching.study_matching_site.exception;

import lombok.Getter;

@Getter
public class InvalidValueException extends BusinessException {

    public InvalidValueException(ErrorCode errorCode) {
        super(errorCode);
    }
}

