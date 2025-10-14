package com.switching.study_matching_site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class KakaoLocalService {

    private final RestTemplate restTemplate;
    @Value("${kakao.api-key}")
    private String kakaoApiKey;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public KakaoLocalService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }
    @RateLimiter(name = "kakaoApi")
    public List<JsonNode> searchStudyRooms(String query, double lng, double lat) throws JsonProcessingException {
        List<JsonNode> results = new ArrayList<>();
        int size = 15;
        int maxPage = 3;

        for (int page = 1; page <= maxPage; page++) {
            String url = String.format(
                    "https://dapi.kakao.com/v2/local/search/keyword.json?query=%s&x=%f&y=%f&radius=2000&page=%d&size=%d",
                    query, lng, lat, page, size
            );
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", kakaoApiKey);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            JsonNode root = objectMapper.readTree(response.getBody()).get("documents");

            if (root == null || root.isEmpty()) break;

            root.forEach(results::add); // JsonNode 그대로 추가
        }

        return results;
    }

    @RateLimiter(name = "kakaoApiBatch")
    @Retry(name = "kakaoApiBatchRetry", fallbackMethod = "onSearchFallBack")
    public JsonNode searchByNameWithRateLimit(String placeName, String address, double lng, double lat) throws JsonProcessingException {
        int size = 15;
        int maxPage = 3;
        int radius = 500;

        for (int page = 1; page <= maxPage; page++) {
            String url = String.format(
                    "https://dapi.kakao.com/v2/local/search/keyword.json?query=%s&x=%f&y=%f&radius=%d&page=%d&size=%d",
                    URLEncoder.encode(placeName, StandardCharsets.UTF_8),
                    lng, lat, radius, page, size
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", kakaoApiKey);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            JsonNode root = objectMapper.readTree(response.getBody()).get("documents");

            if (root == null || root.isEmpty()) break;

            for (JsonNode node : root) {
                if (address.equals(node.path("address_name").asText())) {
                    return node; // 주소가 일치하는 첫 번째 결과 반환
                }
            }
        }

        return null; // 일치하는 결과 없으면 null
    }

    private JsonNode onSearchFallback(String placeName, String address, double lng, double lat, Throwable t) {
        log.warn("Fallback triggered for place={}, address={}, reason={}", placeName, address, t.getMessage());
        return null; // fallback: null 반환 → 비활성화 처리
    }

    /*public String searchStudyRooms(double lng, double lat, String query) {
        String url = String.format(
                "https://dapi.kakao.com/v2/local/search/keyword.json?query=%s&x=%f&y=%f&radius=2000",
                query, lng, lat
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", kakaoApiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }*/

    private List<JsonNode> searchRooms(String query, double lng, double lat) throws JsonProcessingException {
        List<JsonNode> results = new ArrayList<>();
        int size = 15;
        int maxPage = 3;

        for (int page = 1; page <= maxPage; page++) {
            String url = String.format(
                    "https://dapi.kakao.com/v2/local/search/keyword.json?query=%s&x=%f&y=%f&radius=2000&page=%d&size=%d",
                    query, lng, lat, page, size
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoApiKey);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            JsonNode root = objectMapper.readTree(response.getBody()).get("documents");

            if (root == null || root.isEmpty()) break;

            root.forEach(results::add);
        }

        return results;
    }
}
