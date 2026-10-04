package com.tomoe.medassistant.controller;

import com.tomoe.medassistant.agent.AppointmentChainService;
import com.tomoe.medassistant.agent.RoutingWorkflowService;
import com.tomoe.medassistant.agent.orchestrator.OrchestratorService;
import com.tomoe.medassistant.dto.ChatRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/agent")
public class AgentController {

    private final AppointmentChainService appointmentChainService;
    private final RoutingWorkflowService routingWorkflowService;
    private final OrchestratorService orchestratorService;

    @PostMapping("/chain/appointment")
    public ResponseEntity<String> bookAppointmentChain(
            @Valid @RequestBody ChatRequest request,
            @AuthenticationPrincipal Jwt jwt
            ){
        Long userId = jwt.getClaim("userId");

        return ResponseEntity.ok(appointmentChainService
                .bookAppointmentChain(request.prompt(), request.model(), userId));
    }

    @PostMapping("/routing")
    public ResponseEntity<String> routeQuery(
            @Valid @RequestBody ChatRequest request,
            @AuthenticationPrincipal Jwt jwt
    ){
        Long userId = jwt.getClaim("userId");
        return ResponseEntity.ok(routingWorkflowService.routeQuery(request.prompt(), request.model(), userId));
    }

    @PostMapping("orchestrator")
    public ResponseEntity<String> orchestrate(
            @Valid @RequestBody ChatRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = jwt.getClaim("userId");
        return ResponseEntity.ok(orchestratorService.orchestrate(request.prompt(), request.model(), userId));
    }
}
