package com.switching.study_matching_site.controller;

import com.switching.study_matching_site.dto.member.LoginDto;
import com.switching.study_matching_site.dto.member.MemberCreateDto;
import com.switching.study_matching_site.dto.member.MemberReadDto;
import com.switching.study_matching_site.dto.member.MemberUpdateDto;
import com.switching.study_matching_site.service.MemberService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MEMBER API", description = "MEMBER API")
@RestController
@RequestMapping("/studywithmatching.com")
@RequiredArgsConstructor
public class MemberController {

    @Autowired
    private final MemberService memberService;

    @GetMapping("/members/{memberId}")
    public String membersInfo(@PathVariable(name = "memberId") Long memberId) {

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

    @PostMapping("/members")
    public String signMember(@RequestBody @Validated MemberCreateDto memberCreateDto) {
        Long singMemberId = memberService.joinMember(memberCreateDto);
        return singMemberId.toString();
    }

    @DeleteMapping("/members/{memberId}")
    public String deleteMember(@PathVariable(name = "memberId") Long memberId) {
        memberService.leaveMember(memberId);
        return "탈퇴 완료";
    }

    @PutMapping("/members/{memberId}")
    public String updateMember(@PathVariable(name = "memberId") Long memberId,
                               @RequestBody MemberUpdateDto memberUpdateDto) {
        memberService.updateMember(memberId, memberUpdateDto);
        return "수정 완료";
    }

    @PostMapping("/members/login")
    public String loginMember(@RequestBody LoginDto loginDto) {
        MemberCreateDto findMemberCreateDto = memberService.tryLogin(loginDto);
        return findMemberCreateDto.toString();
    }
}
