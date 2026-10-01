package com.tomoe.medassistant.service;

import com.tomoe.medassistant.dto.DrugInfo;

public interface DrugInfoService {
    DrugInfo getDrugInfo(String drugName);
}
