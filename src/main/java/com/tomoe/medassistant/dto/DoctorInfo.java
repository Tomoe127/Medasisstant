package com.tomoe.medassistant.dto;

public record DoctorInfo(
        String firstName,
        String lastName,
        String specialty,
        String licenseNumber,
        String phone,
        String office
) {
}
