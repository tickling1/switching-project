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

    @Operation(summary = "회원 정보", description = "회원 정보를 가져옵니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원 정보 불러오기 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MemberReadDto.class)
                            )
                    )
            })
    @GetMapping("/members")
    public ResponseEntity<MemberReadDto> membersInfo() {
        MemberReadDto memberReadDto = memberService.myInfo();
        return ResponseEntity.ok().body(memberReadDto);
    }

    @Operation(summary = "회원 등록", description = "회원을 등록합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원 등록 성공"
                    )
            })

    @PostMapping("/members/signup")
    public ResponseEntity<Void> signMember(@RequestBody @Validated MemberCreateDto memberCreateDto) {
        Long signMemberId = memberService.joinMember(memberCreateDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원 탈퇴", description = "회원을 탈퇴합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원 탈퇴 성공"
                    )
            })
    @DeleteMapping("/members")
    public ResponseEntity<Void> deleteMember() {
        memberService.leaveMember();
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "회원 수정", description = "회원정보를 수정합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원정보 수정 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MemberUpdateDto.class)
                            )
                    )
            })
    @PutMapping("/members")
    @Parameter(description = "")
    public ResponseEntity<Void> updateMember(@RequestBody MemberUpdateDto memberUpdateDto) {
        memberService.updateMember(memberUpdateDto);
        return ResponseEntity.ok().build();
    }
}
