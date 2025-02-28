package com.switching.study_matching_site.encrypt;

import com.switching.study_matching_site.StudyMatchingSiteApplication;
import org.assertj.core.api.Assertions;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = StudyMatchingSiteApplication.class)
public class JasyptEncryptorTest {

    @Value("${jasypt.encryptor.password}")
    private String secretKey;

    @Test
    @DisplayName("Encrypt Test")
    void test1() {
        String targetText = "This Is Target Text!!";

        // Using Jasypt
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(secretKey);
        encryptor.setAlgorithm("PBEWithMD5AndDES"); //Default

        String encryptedText = encryptor.encrypt(targetText);
        System.out.println("encryptedText = " + encryptedText);

        String decryptedText = encryptor.decrypt(encryptedText);
        System.out.println("decryptedText = " + decryptedText);

        Assertions.assertThat(decryptedText).isEqualTo(targetText);
    }
}
