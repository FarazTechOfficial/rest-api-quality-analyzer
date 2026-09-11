package com.research.analyzer.controller;

import com.research.analyzer.dto.AnalysisSummaryResponse;
import com.research.analyzer.dto.AnalyzeRequest;
import com.research.analyzer.services.AnalysisService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<AnalysisSummaryResponse> analyze(@Valid @RequestBody AnalyzeRequest request) {
        AnalysisSummaryResponse response = analysisService.analyze(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
