package com.tomoe.medassistant.service;

import com.tomoe.medassistant.dto.PatientInfo;

public interface PatientService {
    PatientInfo getPatientInfo(Long patientId);
}
