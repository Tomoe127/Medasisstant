package com.tomoe.medassistant.service;

import com.tomoe.medassistant.config.ClientResolver;
import com.tomoe.medassistant.dto.analysis.ConditionSummary;
import com.tomoe.medassistant.dto.analysis.QueryClassification;
import com.tomoe.medassistant.dto.analysis.SymptomAnalysis;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService{

    private final ClientResolver clientResolver;

    @Value("classpath:prompts/structured-analysis.st")
    private Resource structuredAnalysisResource;

    private PromptTemplate structuredAnalysisTemplate;

    @PostConstruct
    void init(){
        structuredAnalysisTemplate = new PromptTemplate(structuredAnalysisResource);
    }

    @Override
    public ConditionSummary summarizeCondition(String condition, String model) {

        return clientResolver.resolve(model)
                .prompt()
                .user("Proporciona un resumen medico educativo sobre: " + condition)
                .call()
                .entity(ConditionSummary.class);
    }

    @Override
    public List<ConditionSummary> listRelatedCondition(String symptoms, String model) {

        return clientResolver.resolve(model)
                .prompt()
                .user("Lista las 3 condiciones medicas mas probables " +
                        "para estos sintomas: " + symptoms)
                .call()
                .entity(new ParameterizedTypeReference<>() {});
    }

    @Override
    public SymptomAnalysis analyzeSymptoms(String symptoms, String model) {

        String message = structuredAnalysisTemplate.render(
                Map.of("sintomas", symptoms)
        );

        return clientResolver.resolve(model)
                .prompt()
                .user(message)
                .call()
                .entity(SymptomAnalysis.class);
    }

    @Override
    public QueryClassification classifyQuery(String query, String model) {
        return clientResolver.resolve(model)
                .prompt()
                .user("Clasifica la siguiente consulta de un paciente. " +
                        "Determina que tipo de consulta es y explica brevemente por que.\n\n" +
                        "Consulta del paciente: \"" + query + "\"")
                .call()
                .entity(QueryClassification.class);
    }
}
