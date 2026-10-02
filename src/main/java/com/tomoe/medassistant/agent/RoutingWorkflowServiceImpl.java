package com.tomoe.medassistant.agent;

import com.tomoe.medassistant.config.ClientResolver;
import com.tomoe.medassistant.config.MedicalAuditAdvisor;
import com.tomoe.medassistant.dto.analysis.QueryClassification;
import com.tomoe.medassistant.service.AnalysisService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoutingWorkflowServiceImpl implements RoutingWorkflowService{

    private final ClientResolver clientResolver;
    private final AnalysisService analysisService;
    private final MedicalAuditAdvisor medicalAuditAdvisor;

    @Value("classpath:prompts/routing-emergency.st")
    private Resource emergencyResource;

    @Value("classpath:prompts/routing-symptom.st")
    private Resource symptomResource;

    @Value("classpath:prompts/routing-general.st")
    private Resource generalResource;

    @Value("classpath:prompts/routing-prescription.st")
    private Resource prescriptionResource;

    private PromptTemplate emergencyTemplate;
    private PromptTemplate symptomTemplate;
    private PromptTemplate generalTemplate;
    private PromptTemplate prescriptionTemplate;

    @PostConstruct
    void init(){
        emergencyTemplate = new PromptTemplate(emergencyResource);
        symptomTemplate = new PromptTemplate(symptomResource);
        generalTemplate = new PromptTemplate(generalResource);
        prescriptionTemplate = new PromptTemplate(prescriptionResource);
    }


    @Override
    public String routeQuery(String query, String model, Long userId) {
        QueryClassification classification = analysisService.classifyQuery(query, model, userId);

        log.info("Router - tipo: {} - razon: {}", classification.type(), classification.reason());

        return switch (classification.type()){
            case SYMPTOM_REPORT -> handleSymptomReport(query, model, userId);
            case GENERAL_QUESTION -> handleGeneralQuestion(query, model, userId);
            case EMERGENCY -> handleEmergency(query, model, userId);
            case PRESCRIPTION_REQUEST -> handlePrescriptionRequest(query, model, userId);
            case OFF_TOPIC -> handOffTopic(userId);
        };
    }

    private String handleEmergency(String query, String model, Long userId){
        log.info("Ruta: EMERGENCY");

        String message = emergencyTemplate.render(Map.of("consulta", query));

        return ChatClient.create(clientResolver.resolveModel(model))
                .prompt()
                .system("eres un especialista en urgencias medicas. Responde de forma breve y directa")
                .user(message)
                .advisors(a -> a.advisors(medicalAuditAdvisor)
                        .param(ChatMemory.CONVERSATION_ID, String.valueOf(userId)))
                .call()
                .content();
    }


    private String handleSymptomReport(String query, String model, Long userId){
        log.info("Ruta: SYMPTOM_REPORT");

        String message = symptomTemplate.render(Map.of("consulta", query));

        return ChatClient.create(clientResolver.resolveModel(model))
                .prompt()
                .system("eres un especialista en analisis de sintomas")
                .user(message)
                .advisors(a -> a.advisors(medicalAuditAdvisor)
                        .param(ChatMemory.CONVERSATION_ID, String.valueOf(userId)))
                .call()
                .content();
    }


    private String handleGeneralQuestion(String query, String model, Long userId){
        log.info("Ruta: GENERAL_QUESTION");

        String message = generalTemplate.render(Map.of("consulta", query));

        return ChatClient.create(clientResolver.resolveModel(model))
                .prompt()
                .system("eres un medico educador. Responde con detalle y claridad")
                .user(message)
                .advisors(a -> a.advisors(medicalAuditAdvisor)
                        .param(ChatMemory.CONVERSATION_ID, String.valueOf(userId)))
                .call()
                .content();
    }


    private String handlePrescriptionRequest(String query, String model, Long userId){
        log.info("Ruta: PRESCRIPTION_REQUEST");

        String message = prescriptionTemplate.render(Map.of("consulta", query));

        return ChatClient.create(clientResolver.resolveModel(model))
                .prompt()
                .system("eres un farmacologo clinico. Informas sobre medicamentos sin preescribir")
                .user(message)
                .advisors(a -> a.advisors(medicalAuditAdvisor)
                        .param(ChatMemory.CONVERSATION_ID, String.valueOf(userId)))
                .call()
                .content();
    }

    private String handOffTopic(Long userId) {
        log.info("Ruta: OFF_TOPIC - userId: {} - respuesta deterministica, sin llamada al modelo", userId);
        return "Lo siento, solo puedo ayudarte con consultas relacionadas con la salud y medicina. " +
                "Si tienes un consulta medica, no dudes en escribirme.";
    }
}
