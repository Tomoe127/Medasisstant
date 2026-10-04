package com.tomoe.medassistant.agent.orchestrator;

public interface OrchestratorService {

    String orchestrate(String symptoms, String model, Long userId);
}
