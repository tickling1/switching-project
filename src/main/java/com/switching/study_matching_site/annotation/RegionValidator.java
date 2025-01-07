package com.switching.study_matching_site.annotation;

import com.switching.study_matching_site.domain.Region;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class RegionValidator implements ConstraintValidator<ValidRegion, Region> {

    @Override
    public boolean isValid(Region region, ConstraintValidatorContext constraintValidatorContext) {
        // 값이 null이거나 Enum 내 값과 일치하는지 확인
        return region == null || Arrays.asList(Region.values()).contains(region);
    }
}
