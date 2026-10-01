package com.tomoe.medassistant.service;

import com.tomoe.medassistant.dto.analysis.ConditionSummary;
import com.tomoe.medassistant.dto.analysis.QueryClassification;
import com.tomoe.medassistant.dto.analysis.SymptomAnalysis;

import java.util.List;

public interface AnalysisService {
    ConditionSummary summarizeCondition(String condition, String model);
    List<ConditionSummary> listRelatedCondition(String symptoms, String model);
    SymptomAnalysis analyzeSymptoms(String symptoms, String model);
    QueryClassification classifyQuery(String query, String model);
}
