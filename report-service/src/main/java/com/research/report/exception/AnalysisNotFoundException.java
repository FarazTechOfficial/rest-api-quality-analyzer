package com.research.report.exception;

public class AnalysisNotFoundException extends RuntimeException {

    public AnalysisNotFoundException(String analysisId) {
        super("Analysis not found: " + analysisId);
    }
}
