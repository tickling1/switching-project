package com.switching.study_matching_site.controller;

import com.switching.study_matching_site.dto.notice.NoticeRead;
import com.switching.study_matching_site.dto.profile.ProfileCreateDto;
import com.switching.study_matching_site.dto.profile.ProfileReadDto;
import com.switching.study_matching_site.dto.profile.ProfileUpdateDto;
import com.switching.study_matching_site.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "PROFILE", description = "프로필 API")
@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @Operation(summary = "프로필 등록", description = "회원이 프로필을 등록합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "프로필 등록 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProfileCreateDto.class)
                            )
                    )
            })
    @PostMapping("/members/{memberId}/profile")
    public String post(@PathVariable(name = "memberId") Long memberId,
                       @RequestBody @Validated ProfileCreateDto profileCreateDto) {
        return profileService.writeProfile(profileCreateDto, memberId).toString();
    }

    @Operation(summary = "프로필 조회", description = "프로필을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "프로필 등록 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProfileReadDto.class)
                            )
                    )
            })
    @Parameters({
            @Parameter(name = "memberId", description = "members의 id", in = ParameterIn.PATH),
            @Parameter(name = "profileId", description = "프로필 ID", in = ParameterIn.PATH)
    })
    @GetMapping("/members/{memberId}/profile/{profileId}")
    public String read(@PathVariable Long memberId, @PathVariable Long profileId) {
        return profileService.readProfile(memberId, profileId).toString();
    }

    @Operation(summary = "프로필 수정", description = "프로필을 수정합니다.")
    @Parameters({
            @Parameter(name = "memberId", description = "members의 id", in = ParameterIn.PATH),
            @Parameter(name = "profileId", description = "프로필 ID", in = ParameterIn.PATH)
    })
    @PutMapping("/members/{memberId}/profile/{profileId}")
    public String update(@PathVariable Long profileId,
                         @RequestBody ProfileUpdateDto profileUpdateDto) {
        profileService.updateProfile(profileId, profileUpdateDto);
        return "OK";
    }

}
