package com.switching.study_matching_site.controller;

import com.switching.study_matching_site.dto.chat.ChatRead;
import com.switching.study_matching_site.dto.member.LoginDto;
import com.switching.study_matching_site.dto.member.MemberCreateDto;
import com.switching.study_matching_site.dto.member.MemberReadDto;
import com.switching.study_matching_site.dto.member.MemberUpdateDto;
import com.switching.study_matching_site.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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
    @GetMapping("/members/{memberId}")
    public String membersInfo(@Parameter(description = "members의 id", in = ParameterIn.PATH)
                                  @PathVariable(name = "memberId") Long memberId) {

        /* // Header에서 loginId 를 가져옴
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        
        Long findMemberId = memberService.findMemberId(loginId);
        System.out.println("security loginId = " + loginId);*/

        /*if (!findMemberId.equals(memberId)) {
            return "잘못된 사용자 접근입니다.";
        }
*/
        MemberReadDto memberReadDto = memberService.myInfo(memberId);
        return memberReadDto.toString();
    }

    @Operation(summary = "회원 등록", description = "회원을 등록합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원 등록 성공"
                    )
            })
    @PostMapping("/members")
    public String signMember(@RequestBody @Validated MemberCreateDto memberCreateDto) {
        Long signMemberId = memberService.joinMember(memberCreateDto);
        return signMemberId.toString();
    }

    @Operation(summary = "회원 탈퇴", description = "회원을 탈퇴합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원 탈퇴 성공"
                    )
            })
    @DeleteMapping("/members/{memberId}")
    public String deleteMember(@Parameter(description = "members의 id", in = ParameterIn.PATH)
                                   @PathVariable(name = "memberId") Long memberId) {
        memberService.leaveMember(memberId);
        return "탈퇴 완료";
    }

    @Operation(summary = "회원 수정", description = "회원정보를 수정합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원정보 수정 성공"
                    )
            })
    @PutMapping("/members/{memberId}")
    public String updateMember(@Parameter(description = "members의 id")
                                   @PathVariable(name = "memberId") Long memberId,
                               @RequestBody MemberUpdateDto memberUpdateDto) {
        memberService.updateMember(memberId, memberUpdateDto);
        return "수정 완료";
    }

    @Operation(summary = "회원 로그인", description = "회원로그인을 시도합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원 로그인 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = MemberCreateDto.class
                                    )
                            )
                    )
            })
    @PostMapping("/members/login")
    public String loginMember(@RequestBody LoginDto loginDto) {
        MemberCreateDto findMemberCreateDto = memberService.tryLogin(loginDto);
        return findMemberCreateDto.toString();
    }
}
