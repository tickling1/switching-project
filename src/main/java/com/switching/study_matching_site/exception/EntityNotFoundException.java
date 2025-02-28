package com.switching.study_matching_site.exception;


public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);

    }
}
