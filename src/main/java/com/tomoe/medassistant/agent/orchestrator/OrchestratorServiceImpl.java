package com.tomoe.medassistant.agent.orchestrator;

import com.tomoe.medassistant.config.ClientResolver;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrchestratorServiceImpl implements OrchestratorService{

    private final ClientResolver clientResolver;
    private final ClinicalWorker clinicalWorker;
    private final HistoryWorker historyWorker;
    private final BibliographyWorker bibliographyWorker;

    @Value("classpath:prompts/orchestrator-synthesis.st")
    private Resource synthesisResource;

    private PromptTemplate systhesisTemplate;

    @PostConstruct
    void init() {
        systhesisTemplate = new PromptTemplate(synthesisResource);
    }


    @Override
    public String orchestrate(String symptoms, String model, Long userId) {

        log.info("Orquestador - iniciando analisis integral");

        String clinicalAnalysis = clinicalWorker.analyze(symptoms, model);
        String patientHistory = historyWorker.lookup(userId);
        String bibliographyResearch = bibliographyWorker.research(symptoms, model);

        log.info("Orquestador - los tres workers completaron su trabajo");

        return synthesize(clinicalAnalysis, patientHistory, bibliographyResearch, symptoms, model);
    }

    private String synthesize(String clinicalAnalysis, String patientHistory,
                              String bibliographyResearch, String symptoms, String model){

        String prompt = systhesisTemplate.render(Map.of(
                "analisisClinico", clinicalAnalysis,
                "historial", patientHistory,
                "bibliografia", bibliographyResearch,
                "sintomas", symptoms
        ));

        String result = ChatClient.create(clientResolver.resolveModel(model))
                .prompt()
                .system("Eres un medico que integra informacion de multiples fuentes para dar una respuesta completa.")
                .user(prompt)
                .call()
                .content();

        log.info("Orquestador - sintesis completa");
        return result;
    }
}
