package com.tomoe.medassistant.repository;

import com.tomoe.medassistant.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
