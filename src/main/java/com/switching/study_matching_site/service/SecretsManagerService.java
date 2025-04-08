package com.switching.study_matching_site.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.Map;

@Service
public class SecretsManagerService {

    private final SecretsManagerClient secretsClient;
    private final ObjectMapper objectMapper;
    private final String secretName;

    public static void main(String[] args) {
        SecretsManagerService secretsManagerService = new SecretsManagerService("prod/switching");
        secretsManagerService.getSecret();
    }

    public SecretsManagerService(@Value("${custom.secret.name}") String secretName) {
        this.secretsClient = SecretsManagerClient.builder()
                .region(Region.AP_NORTHEAST_2) // 서울 리전
                .build();
        this.objectMapper = new ObjectMapper();
        this.secretName = secretName;
    }

    public void getSecret() {

        Region region = Region.of("ap-northeast-2");

        // Create a Secrets Manager client
        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(region)
                .build();

        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse getSecretValueResponse;

        try {
            getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
        } catch (Exception e) {
            // For a list of exceptions thrown, see
            // https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
            throw e;
        }

        String secret = getSecretValueResponse.secretString();

        // Your code goes here.
        System.out.println(secret);
    }
}
