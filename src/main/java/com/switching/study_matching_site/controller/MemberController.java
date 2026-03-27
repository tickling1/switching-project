package com.switching.study_matching_site.controller;

import com.switching.study_matching_site.dto.member.MemberCreateDto;
import com.switching.study_matching_site.dto.member.MemberReadDto;
import com.switching.study_matching_site.dto.member.MemberUpdateDto;
import com.switching.study_matching_site.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MEMBER", description = "회원 API")
@RestController
@RequiredArgsConstructor
public class MemberController {

    @Autowired
    private final MemberService memberService;

    @Operation(
            summary = "내 정보 조회",
            description = "헤더의 JWT 토큰을 이용해 현재 로그인한 회원의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = MemberReadDto.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패 (토큰 없음/만료)")
    })
    @GetMapping("/members")
    public ResponseEntity<MemberReadDto> membersInfo() {
        MemberReadDto memberReadDto = memberService.myInfo();
        return ResponseEntity.ok().body(memberReadDto);
    }

    @Operation(
            summary = "회원 가입",
            description = "새로운 회원을 등록합니다. 아이디, 비밀번호, 닉네임 등의 정보를 입력받습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 등록 성공"),
            @ApiResponse(responseCode = "400", description = "입력값 유효성 검사 실패 (아이디 중복 등)")
    })
    @PostMapping("/members/signup")
    public ResponseEntity<Void> signMember(@RequestBody @Validated MemberCreateDto memberCreateDto) {
        Long signMemberId = memberService.joinMember(memberCreateDto);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "회원 탈퇴",
            description = "현재 로그인한 계정을 삭제(탈퇴) 처리합니다. 탈퇴 시 관련 데이터가 모두 삭제되거나 비활성화됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @DeleteMapping("/members")
    public ResponseEntity<Void> deleteMember() {
        memberService.leaveMember();
        return ResponseEntity.ok().build();
    }


    @Operation(
            summary = "회원 정보 수정",
            description = "현재 로그인한 회원의 닉네임, 프로필 이미지, 자기소개 등 정보를 업데이트합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력 값 (유효성 검사 실패)"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PutMapping("/members")
    public ResponseEntity<Void> updateMember(@RequestBody MemberUpdateDto memberUpdateDto) {
        memberService.updateMember(memberUpdateDto);
        return ResponseEntity.ok().build();
    }
}
