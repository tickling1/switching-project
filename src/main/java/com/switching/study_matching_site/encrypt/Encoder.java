package com.switching.study_matching_site.encrypt;

import com.google.common.hash.Hashing;
import lombok.Getter;

import java.nio.charset.StandardCharsets;

@Getter
public class Encoder {

    private String password;
    private static final String salt = "ENCRYPT_SALT";

    public Encoder(String password) {
        this.password = hashingAndSalting(password);
    }

    public static String hashingAndSalting(String password) {
        String salting = salt + password;
        return Hashing.sha256().hashString(salting, StandardCharsets.UTF_8).toString();
    }
}
