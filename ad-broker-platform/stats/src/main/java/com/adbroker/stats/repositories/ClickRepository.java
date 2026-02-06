package com.adbroker.stats.repositories;

import com.adbroker.stats.dto.StatPoint;
import com.adbroker.stats.entities.ClickRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClickRepository extends JpaRepository<ClickRecord, String> {

    Long countByCampaignId(String campaignId);

    @Query("SELECT new com.adbroker.stats.dto.StatPoint(c.country, COUNT(c)) " +
            "FROM ClickRecord c " +
            "WHERE c.campaignId = :campaignId " +
            "GROUP BY c.country " +
            "ORDER BY COUNT(c) DESC")
    List<StatPoint> countClicksByCountry(@Param("campaignId") String campaignId);

    @Query(value = """
        SELECT 
            TO_CHAR(clicked_at, 'YYYY-MM-DD HH24:00') as time_label, 
            COUNT(*) as cnt
        FROM stats.ad_clicks 
        WHERE campaign_id = :campaignId
        GROUP BY time_label
        ORDER BY time_label ASC
        """, nativeQuery = true)
    List<Object[]> countClicksByHour(@Param("campaignId") String campaignId);
}