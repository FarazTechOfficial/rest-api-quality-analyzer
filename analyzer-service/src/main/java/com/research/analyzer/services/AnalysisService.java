package com.research.analyzer.services;

import com.research.analyzer.dto.AnalysisSummaryResponse;
import com.research.analyzer.dto.AnalyzeRequest;

public interface AnalysisService {

    AnalysisSummaryResponse analyze(AnalyzeRequest request);
}