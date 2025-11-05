package com.switching.study_matching_site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.switching.study_matching_site.SecurityUtil;
import com.switching.study_matching_site.UserRateLimiter;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.exception.ErrorCode;
import com.switching.study_matching_site.exception.RateLimitException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
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
    private final UserRateLimiter userRateLimiter;
    private final ObjectMapper objectMapper;
    private final SecurityUtil securityUtil;
    private final String kakaoApiKey;

    public KakaoLocalService(RestTemplateBuilder restTemplateBuilder,
                             @Value("${kakao.api.key}") String kakaoApiKey,
                             SecurityUtil securityUtil) {
        this.restTemplate = restTemplateBuilder.build();
        this.userRateLimiter = new UserRateLimiter();
        this.objectMapper = new ObjectMapper();
        this.kakaoApiKey = kakaoApiKey;
        this.securityUtil = securityUtil;
    }

    public List<JsonNode> searchStudyRooms(String query, double lng, double lat) throws JsonProcessingException {

        Member member = securityUtil.getMemberByUserDetails();
        String userId = member.getId().toString();

        boolean allowed = userRateLimiter.tryConsume(userId);
        if (!allowed) {
            log.warn("User {} exceeded rate limit", member.getLoginId());
            throw new RateLimitException(ErrorCode.TOO_MANY_REQUESTS);
        }

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
