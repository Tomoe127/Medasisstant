package com.tomoe.medassistant.dto.analysis;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record PossibleCondition(

        @JsonPropertyDescription("Nombre de la condicion medica")
        String name,

        @JsonPropertyDescription("Explicacion breve y accesible de la condicion")
        String description,

        @JsonPropertyDescription("Nivel de gravedad de esta condicion especifica")
        Severity severity
) {
}
