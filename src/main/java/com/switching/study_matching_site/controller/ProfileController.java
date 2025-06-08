package com.switching.study_matching_site.controller;

import com.switching.study_matching_site.dto.profile.ProfileCreateDto;
import com.switching.study_matching_site.dto.profile.ProfileReadDto;
import com.switching.study_matching_site.dto.profile.ProfileUpdateDto;
import com.switching.study_matching_site.service.ProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @PostMapping("/profile")
    public ResponseEntity<Void> postProfile(@RequestBody @Validated ProfileCreateDto profileCreateDto) {
        profileService.writeProfile(profileCreateDto);
        return new ResponseEntity<>(HttpStatus.OK);
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

    @Parameter(name = "profileId", description = "프로필 ID", in = ParameterIn.PATH)
    @GetMapping("/profile/{profileId}")
    public String read( @PathVariable Long profileId) {
        return profileService.readProfile(profileId).toString();
    }

    @Operation(summary = "프로필 수정", description = "프로필을 수정합니다.")
    @Parameter(name = "ProfileUpdateDto", description = "회원 프로필 수정 DTO")
    @PutMapping("/profile")
    public ResponseEntity<Void> update(@RequestBody ProfileUpdateDto profileUpdateDto) {
        profileService.updateProfile( profileUpdateDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
