package com.example.autobriefing.repository;

import com.example.autobriefing.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    News findFirstByOrderByTimeBlockDesc();

    @Query("SELECT n FROM News n WHERE n.timeBlock >= :startTime ORDER BY n.timeBlock DESC")
    List<News> findAllNewsFromLast24Hours(@Param("startTime") LocalDateTime startTime);
}
