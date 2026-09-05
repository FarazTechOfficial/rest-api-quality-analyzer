package com.research.analyzer.rule;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiSpecification;

public interface RestApiRule {

    String getRuleId();

    String getRuleName();

    String getPracticeId();

    String getDescription();

    RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification);
}
