package com.tomoe.medassistant.repository;

import com.tomoe.medassistant.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctorIdInAndDateAndAvailableTrue(List<Long> doctorIds, LocalDate date);
    List<Appointment> findByDoctorIdInAndDateAndStartTimeAndAvailableTrue(List<Long> doctorIds, LocalDate date, LocalTime startTime);
}
