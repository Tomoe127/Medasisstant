package com.tomoe.medassistant.controller;

import com.tomoe.medassistant.dto.ChatRequest;
import com.tomoe.medassistant.service.AssistantService;
import com.tomoe.medassistant.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final AssistantService assistantService;
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<String> chat(
            @RequestBody ChatRequest request,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication
    ) {
        Long userId = jwt.getClaim("userId");
        String role = securityUtils.extractRole(authentication);
        return ResponseEntity.ok(assistantService.chat(request.prompt(), request.model(), userId, role));
    }

    @PostMapping(value = "/stream", produces = "text/event-stream; charset=UTF-8")
    public Flux<String> chatStream(
            @RequestBody ChatRequest request,
            @AuthenticationPrincipal Jwt jwt,
            Authentication authentication
    ) {
        Long userId = jwt.getClaim("userId");
        String role = securityUtils.extractRole(authentication);
        return assistantService.chatStream(request.prompt(), request.model(), userId, role);
    }

    @PostMapping("/explain")
    public ResponseEntity<String> explainCondition(
            @Valid @RequestBody ChatRequest request,
            @AuthenticationPrincipal Jwt jwt
    ){
        Long userId = jwt.getClaim("userId");
        return ResponseEntity.ok(assistantService.explainCondition(request.prompt(), request.model(), userId));
    }


    @PostMapping("/symptoms")
    public ResponseEntity<String> analyzeSymptoms(
            @Valid @RequestBody ChatRequest request,
            @AuthenticationPrincipal Jwt jwt
    ){
        Long userId = jwt.getClaim("userId");
        return ResponseEntity.ok(assistantService.analyzeSymptoms(request.prompt(), request.model(), userId));
    }

    @PostMapping("/diagnose")
    public ResponseEntity<String> diagnoseWithReasoning(
            @Valid @RequestBody ChatRequest request,
            @AuthenticationPrincipal Jwt jwt
    ){
        Long userId = jwt.getClaim("userId");
        return ResponseEntity.ok(assistantService.diagnoseWithReasoning(request.prompt(), request.model(), userId));
    }

    @PostMapping("/consult")
    public ResponseEntity<String> consult(
            @Valid @RequestBody ChatRequest request,
            @AuthenticationPrincipal Jwt jwt
    ){
        Long userId = jwt.getClaim("userId");
        return ResponseEntity.ok(assistantService.consult(request.prompt(), request.model(), userId));
    }

}
