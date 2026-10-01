package com.tomoe.medassistant.service;

import com.tomoe.medassistant.dto.DoctorInfo;

import java.util.List;

public interface DoctorService {
    List<DoctorInfo> searchDoctors(String query);
}
