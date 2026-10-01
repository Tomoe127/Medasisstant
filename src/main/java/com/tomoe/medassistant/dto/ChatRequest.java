package com.tomoe.medassistant.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(
        @NotBlank(message = "El prompt no puede estar vacio")
        String prompt,
        String model
) {
}
