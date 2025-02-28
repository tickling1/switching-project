package com.switching.study_matching_site.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common
    INVALID_INPUT_VALUE(400, "C001", "잘못된 형식의 값을 입력했습니다."),

    // Member
    MEMBER_NOT_FOUND(404, "M001", "회원을 찾지 못했습니다."),
    LOGIN_ID_DUPLICATION(400, "M002", "가입된 아이디가 이미 존재합니다."),
    EMAIL_DUPLICATION(400, "M003", "가입된 이메일이 이미 존재합니다."),

    // Login
    LOGIN_INPUT_INVALID(401, "M004", "아이디 또는 비밀번호가 잘못되었습니다."),
    ACCESS_DENIED(403, "M005", "접근할 권한이 없습니다."),

    // Participation
    ROOM_NOT_FOUND(404, "R001", "방을 찾지 못했습니다."),
    ROOM_FULL(400, "R002", "방이 가득찼습니다."),
    ALREADY_PARTICIPATED(409, "R003", "방에 이미 참여중입니다."),
    ROOM_FAILED_REMOVE(400, "R004", "방을 삭제하는데 실패하였습니다. 방에 있는 인원들이 모두 나가야 합니다."),

    // Friends
    FRIEND_NOT_FOUND(404, "F001", "찾으려는 친구가 존재하지 않습니다."),
    NOT_FRIEND_RELATIONSHIP(404, "F002", "친구 상태가 아닙니다."),

    // Chat
    CHAT_NOT_FOUND(404, "T001", "채팅을 찾을 수 없습니다."),

    // Profile
    PROFILE_NOT_FOUND(404, "F001", "프로필을 찾지 못했습니다. 먼저 프로필을 작성해주세요."),
    PROFILE_ALREADY_EXISTS(409, "F002", "프로필이 이미 존재합니다."),

    // Notice
    NOTICE_NOT_FOUND(404, "N001", "공지사항을 찾을 수 없습니다."),
    NOTICE_FORBIDDEN(403, "N002", "방장 권한이 필요합니다."),

    // JWT
    JWT_TOKEN_MISSING(401, "J001", "JWT 토큰이 필요합니다."),
    JWT_TOKEN_MALFORMED(400, "J002","잘못된 형식의 JWT 토큰입니다."),
    JWT_TOKEN_EXPIRED(401, "J003","JWT 토큰이 만료되었습니다."),
    JWT_TOKEN_INVALID(403, "J004","JWT 토큰이 유효하지 않습니다."),
    JWT_TOKEN_TAMPERED(403, "J005","JWT 토큰이 변조되었습니다.");

    private final String code;
    private final String message;
    private final int status;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }
}
