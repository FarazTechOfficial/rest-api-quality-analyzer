package com.research.report.repository;

import com.research.report.entity.ApiAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApiAnalysisRepository extends JpaRepository<ApiAnalysis, String> {

    List<ApiAnalysis> findByApiNameContainingIgnoreCaseOrderByAnalyzedAtDesc(String apiName);

    List<ApiAnalysis> findByOrderByAnalyzedAtDesc();
}
