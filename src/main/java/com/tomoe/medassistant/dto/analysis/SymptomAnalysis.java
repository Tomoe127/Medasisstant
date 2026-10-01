package com.tomoe.medassistant.dto.analysis;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

public record SymptomAnalysis(

        @JsonPropertyDescription("Lista de sintomas identificados en la consulta del paciente")
        List<String> indentifiedSymptoms,

        @JsonPropertyDescription("Posibles condiciones medicas basadas en los sintomas, ordenadas de mas a menos probable")
        List<PossibleCondition> possibleConditions,

        @JsonPropertyDescription("Nivel de urgencia general de la situacion del paciente")
        Urgency urgencylevel,

        @JsonPropertyDescription("Recomendacion general para el paciente, incluyendo si debe buscar atencion medica")
        String recommendation
) {
}
