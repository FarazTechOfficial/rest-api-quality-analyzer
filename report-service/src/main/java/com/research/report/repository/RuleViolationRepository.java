package com.research.report.repository;

import com.research.report.entity.RuleViolation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RuleViolationRepository extends JpaRepository<RuleViolation, Long> {

    List<RuleViolation> findByAnalysis_Id(String analysisId);
}
