package com.switching.study_matching_site.controller;

import com.switching.study_matching_site.dto.profile.ProfileCreateDto;
import com.switching.study_matching_site.dto.profile.ProfileReadDto;
import com.switching.study_matching_site.dto.profile.ProfileUpdateDto;
import com.switching.study_matching_site.service.ProfileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "PROFILE API", description = "PROFILE API")
@RestController
@RequestMapping("/studywithmatching.com")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/members/{memberId}/profile")
    public String post(@PathVariable(name = "memberId") Long memberId,
                       @RequestBody @Validated ProfileCreateDto profileCreateDto) {
        return profileService.writeProfile(profileCreateDto, memberId).toString();
    }

    @GetMapping("/members/{memberId}/profile/{profileId}")
    public String read(@PathVariable Long memberId, @PathVariable Long profileId) {
        return profileService.readProfile(memberId, profileId).toString();
    }

    @PutMapping("/members/{memberId}/profile/{profileId}")
    public String update(@PathVariable Long profileId, @RequestBody ProfileUpdateDto profileUpdateDto) {
        profileService.updateProfile(profileId, profileUpdateDto);
        return "OK";
    }

}
