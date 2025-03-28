package com.switching.study_matching_site.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "회원 수정 요청 DTO")
public class MemberUpdateDto {

    //@NotNull
    @Schema(description = "회원 이름")
    @Length(min = 3, max = 7)
    private String username;

    @Schema(description = "회원 비밀번호", example = "lucky123!")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$",
            message = "* 비밀번호 길이는 8자 이상 30자 이하.\n" +
                    "     * 영문자, 숫자, 특수문자 각각 최소 1개 이상 포함.\n" +
                    "     * (허용된 특수문자: @$!%*#?&)")
    private String password;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{5,20}@[A-Za-z]{2,}\\.[A-Za-z]{2,}$",
            message = "올바른 이메일 형식이 아닙니다.")
    @Schema(description = "회원 이메일", example = "example@gmail.com")
    private String email;

    @Schema(description = "회원 핸드폰 번호", example = "010-1111-2222")
    @Length(max = 13)
    @Pattern(regexp = "^01([0|1|6|7|8|9])-?([0-9]{3,4})-?([0-9]{4})$",
            message = "올바른 휴대폰 번호 형식이 아닙니다. ex) 010-1111-2222")
    private String phoneNumber;

}
