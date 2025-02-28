package com.switching.study_matching_site.dto;

import com.switching.study_matching_site.dto.member.MemberCreateDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import java.util.Set;

class MemberDtoTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    public static void init() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void loginIdValid() {
        String loginId = "test";
        String password = "password1";
        MemberCreateDto memberCreateDto = MemberCreateDto.builder()
                .loginId(loginId)
                .password(password)
                .build();

        Set<ConstraintViolation<MemberCreateDto>> violations = validator.validate(memberCreateDto);
        for (ConstraintViolation<MemberCreateDto> violation : violations) {
            System.err.print(violation.getMessage());
            System.out.println();
        }
    }

}