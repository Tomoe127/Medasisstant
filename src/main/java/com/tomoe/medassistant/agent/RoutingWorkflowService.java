package com.tomoe.medassistant.agent;

public interface RoutingWorkflowService {
    String routeQuery(String query, String model, Long userId);
}
