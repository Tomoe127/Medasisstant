package com.tomoe.medassistant.dto.analysis;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record QueryClassification(
        @JsonPropertyDescription("Tipo de consulta identificada")
        QueryType type,

        @JsonPropertyDescription("Explicacion breve de por que se clasifico de esta manera")
        String reason
) {
}
