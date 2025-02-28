package com.switching.study_matching_site.exception;

import lombok.Getter;

@Getter
public class JWTException extends BusinessException{

    public JWTException(ErrorCode errorCode) {
        super(errorCode);
    }
}
