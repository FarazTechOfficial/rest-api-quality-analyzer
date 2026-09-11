package com.research.report.transfer;

import com.research.report.dto.RuleResultDto;
import com.research.report.entity.ApiAnalysis;
import com.research.report.entity.RuleViolation;
import org.springframework.stereotype.Component;

@Component
public class RuleViolationTransformer {

    public RuleViolation toEntity(RuleResultDto result, ApiAnalysis analysis) {
        RuleViolation violation = new RuleViolation();
        violation.setAnalysis(analysis);
        violation.setRuleId(result.getRuleId());
        violation.setRuleName(result.getRuleName());
        violation.setPracticeId(result.getPracticeId());
        violation.setEndpoint(result.getEndpoint());
        violation.setMethod(result.getMethod());
        violation.setStatus(result.getStatus());
        violation.setPassed(result.isPassed());
        violation.setMessage(result.getMessage());
        violation.setRecommendation(result.getRecommendation());
        return violation;
    }

    public RuleResultDto toDto(RuleViolation violation) {
        RuleResultDto dto = new RuleResultDto();
        dto.setRuleId(violation.getRuleId());
        dto.setRuleName(violation.getRuleName());
        dto.setPracticeId(violation.getPracticeId());
        dto.setEndpoint(violation.getEndpoint());
        dto.setMethod(violation.getMethod());
        dto.setStatus(violation.getStatus());
        dto.setPassed(violation.isPassed());
        dto.setMessage(violation.getMessage());
        dto.setRecommendation(violation.getRecommendation());
        return dto;
    }
}