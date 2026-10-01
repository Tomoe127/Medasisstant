package com.tomoe.medassistant.tools;

import com.tomoe.medassistant.dto.AppointmentInfo;
import com.tomoe.medassistant.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentSearchTool {
    private final AppointmentService appointmentService;

    @Tool(description = "Buscar turnos medicos disponibles para una especialidad y fecha. Usar cuando el " +
        "usuario pregunte por disponibilidad de turnos o citas medicas.")
    public List<AppointmentInfo> searchAppointments(
            @ToolParam(description = "Especialidad medica, por ejemplo: cardiologia, pediatria, dermatologia")
                        String specialty,
            @ToolParam(description = "Fecha de la cita en formato yyyy-MM-dd")
                        String date
    ){
        return appointmentService.findAvailableAppointments(specialty, LocalDate.parse(date));
    }
}
