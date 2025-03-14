package com.switching.study_matching_site.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Refresh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loginId;

    private String refresh;
    
    // 만료 시간
    private String expiration;

    public Refresh(String loginId, String refresh, String expiration) {
        this.loginId = loginId;
        this.refresh = refresh;
        this.expiration = expiration;
    }
}
