package com.switching.study_matching_site.encrypt;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class EncoderTest {
    
    @Test
    @DisplayName("비밀번호 암호화 테스트")
    public void EncoderTest() throws Exception {
        // given
        String password = "sw123";
        // when
        String hashData = Encoder.hashingAndSalting(password);
        // then
        Assertions.assertThat(hashData).
                isEqualTo(Encoder.hashingAndSalting("sw123"))
                .as("동일 값 확인");
    }

}