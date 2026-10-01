package com.tomoe.medassistant.dto.analysis;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record ConditionSummary(
        @JsonPropertyDescription("Nombre de la condicion medica")
        String conditionName,

        @JsonPropertyDescription("Descripcion accesible para pacientes, sin jerga medica innecesaria")
        String description,

        @JsonPropertyDescription("Sintomas mas frecuentes asociados a esta condicion")
        String commonSymptoms,

        @JsonPropertyDescription("Nivel de gravedad general de la condicion")
        Severity severity,

        @JsonPropertyDescription("Indicaciones claras de cuando buscar atencion medica profesional")
        String whenToSeeDoctor
) {
}
