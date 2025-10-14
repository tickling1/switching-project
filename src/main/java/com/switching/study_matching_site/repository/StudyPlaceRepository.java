package com.switching.study_matching_site.repository;

import com.switching.study_matching_site.domain.StudyPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StudyPlaceRepository extends JpaRepository<StudyPlace, Long> {

    // 모든 데이터 조회 (데이터 양이 많으면 성능 부담 → Bounding Box 추천)
    @Query("SELECT s FROM StudyPlace s")
    List<StudyPlace> findAllRooms();

    @Query("""
        SELECT sp FROM StudyPlace sp
        WHERE sp.isActive = true
          AND sp.lastCheckedAt >= :threshold
    """)
    List<StudyPlace> findRecentPlaces(@Param("threshold") LocalDateTime threshold);


    // 위도/경도 반올림 범위 기반 최근 조회
    @Query("""
    SELECT sp FROM StudyPlace sp
    WHERE sp.isActive = true
      AND sp.lastCheckedAt >= :cutoff
      AND sp.lat BETWEEN :lat - :latRange AND :lat + :latRange
      AND sp.lng BETWEEN :lng - :lngRange AND :lng + :lngRange
    """)
    List<StudyPlace> findRecentNearby(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("latRange") double latRange,
            @Param("lngRange") double lngRange,
            @Param("cutoff") LocalDateTime cutoff
    );


    Optional<StudyPlace> findByPlaceNameAndAddress (String placeName, String address);

    List<StudyPlace> findDistinctByPlaceNameAndAddress (String placeName, String address);

    @Query("SELECT COUNT(*) FROM StudyPlace WHERE geohash = :geohash")
    Integer countByGeohash(@Param("geohash") String geohash);


    // 격자 지오해시
    @Query("SELECT DISTINCT s FROM StudyPlace s WHERE s.geohash IN :prefixes AND s.isActive = true")
    List<StudyPlace> findByGeohashIn(@Param("prefixes") List<String> prefixes);

    List<StudyPlace> findByLastCheckedAtBefore(LocalDateTime threshold);
}
