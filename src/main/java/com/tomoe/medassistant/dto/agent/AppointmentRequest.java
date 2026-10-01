package com.tomoe.medassistant.dto.agent;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record AppointmentRequest(
        @JsonPropertyDescription("Especialidad medica solicitada, por ejemplo: cardiologia, pediatria, dermatologia")
        String speciality,

        @JsonPropertyDescription("Fecha solicitada en formato yyyy-MM-dd")
        String date
) {
}
