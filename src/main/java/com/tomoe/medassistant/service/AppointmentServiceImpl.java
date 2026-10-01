package com.tomoe.medassistant.service;

import com.tomoe.medassistant.dto.AppointmentInfo;
import com.tomoe.medassistant.model.Appointment;
import com.tomoe.medassistant.model.Doctor;
import com.tomoe.medassistant.repository.AppointmentRepository;
import com.tomoe.medassistant.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImpl implements AppointmentService{

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    public List<AppointmentInfo> findAvailableAppointments(String specialty, LocalDate date) {
        var doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);

        if (doctors.isEmpty()){
            return List.of();
        }

        var doctorNames = buildDoctorNameMap(doctors);
        var doctorIds = new ArrayList<>(doctorNames.keySet());
        var appointments = appointmentRepository.findByDoctorIdInAndDateAndAvailableTrue(doctorIds, date);

        return toAppointmentInfoList(appointments, doctorNames, specialty);
    }

    @Transactional
    @Override
    public String bookAppointment(String specialty, LocalDate date, LocalTime time, Long patientId) {

        log.info("Reservando turno: specialty={}, date={}, time={}, patientId={}",
                specialty, date, time, patientId);

        var doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        if (doctors.isEmpty()){
            return "No se encontro la especialidad: " + specialty;
        }

        var doctorIds = doctors.stream().map(Doctor::getId).toList();
        var appointment = findAvailableAppointment(doctorIds, date, time);
        if (appointment==null){
            return "El turno no esta disponible.";
        }

        appointment.setAvailable(false);
        appointment.setPatientId(patientId);

        appointmentRepository.save(appointment);

        return "Turno reservado exitosamente.";
    }

    @Transactional(readOnly = true)
    private Appointment findAvailableAppointment(List<Long> doctorIds, LocalDate date, LocalTime time){
        return appointmentRepository.findByDoctorIdInAndDateAndStartTimeAndAvailableTrue(doctorIds,date,time)
                .stream()
                .findFirst()
                .orElse(null);
    }

    private Map<Long, String> buildDoctorNameMap(List<Doctor> doctors){
        return doctors.stream()
                .collect(Collectors.toMap(
                        Doctor::getId,
                        d -> d.getFirstName() + " " + d.getLastName()
                ));
    }

    private List<AppointmentInfo> toAppointmentInfoList(
            List<Appointment> appointments,
            Map<Long, String> doctorNames,
            String specialty
    ){
        return appointments.stream()
                .map(a -> new AppointmentInfo(
                        doctorNames.get(a.getDoctorId()),
                        specialty,
                        a.getDate().toString(),
                        a.getStartTime().toString()
                )).toList();
    }
}
