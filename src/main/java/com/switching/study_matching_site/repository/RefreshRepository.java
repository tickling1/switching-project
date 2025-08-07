package com.switching.study_matching_site.repository;

import com.switching.study_matching_site.domain.Refresh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RefreshRepository extends JpaRepository<Refresh, Long> {

    Boolean existsByRefresh(String refresh);
    long deleteByRefresh(String refresh); // 삭제된 row 수 반환
    void deleteByLoginId(String loginId);
}
