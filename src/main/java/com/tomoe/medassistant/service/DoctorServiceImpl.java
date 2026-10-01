package com.tomoe.medassistant.service;

import com.tomoe.medassistant.dto.DoctorInfo;
import com.tomoe.medassistant.model.Doctor;
import com.tomoe.medassistant.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorServiceImpl implements DoctorService{

    private final DoctorRepository doctorRepository;

    @Transactional(readOnly = true)
    @Override
    public List<DoctorInfo> searchDoctors(String query) {
        return doctorRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrSpecialtyContainingIgnoreCase(
                        query, query, query
                )
                .stream()
                .map(this::toDoctorInfo)
                .toList();
    }

    private DoctorInfo toDoctorInfo(Doctor doctor) {
        return new DoctorInfo(
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getSpecialty(),
                doctor.getLicenseNumber(),
                doctor.getPhone(),
                doctor.getOffice());
    }
}
