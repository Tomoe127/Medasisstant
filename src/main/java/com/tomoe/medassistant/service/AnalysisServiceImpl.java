package com.tomoe.medassistant.service;

import com.tomoe.medassistant.config.ClientResolver;
import com.tomoe.medassistant.dto.analysis.ConditionSummary;
import com.tomoe.medassistant.dto.analysis.QueryClassification;
import com.tomoe.medassistant.dto.analysis.SymptomAnalysis;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
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
    public ConditionSummary summarizeCondition(String condition, String model, Long userId) {

        return clientResolver.resolve(model)
                .prompt()
                .user("Proporciona un resumen medico educativo sobre: " + condition)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, String.valueOf(userId)))
                .call()
                .entity(ConditionSummary.class);
    }

    @Override
    public List<ConditionSummary> listRelatedCondition(String symptoms, String model, Long userId) {

        return clientResolver.resolve(model)
                .prompt()
                .user("Lista las 3 condiciones medicas mas probables " +
                        "para estos sintomas: " + symptoms)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, String.valueOf(userId)))
                .call()
                .entity(new ParameterizedTypeReference<>() {});
    }

    @Override
    public SymptomAnalysis analyzeSymptoms(String symptoms, String model, Long userId) {

        String message = structuredAnalysisTemplate.render(
                Map.of("sintomas", symptoms)
        );

        return clientResolver.resolve(model)
                .prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, String.valueOf(userId)))
                .call()
                .entity(SymptomAnalysis.class);
    }

    @Override
    public QueryClassification classifyQuery(String query, String model, Long userId) {
        log.info("Clasificacion de consulta - modelo: {}", model);

        return ChatClient.create(clientResolver.resolveModel(model))
                .prompt()
                .user("Clasifica la siguiente consulta de un paciente. " +
                        "Determina que tipo de consulta es y explica brevemente por que.\n\n" +
                        "Consulta del paciente: \"" + query + "\"")
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, String.valueOf(userId)))
                .call()
                .entity(QueryClassification.class);
    }
}
