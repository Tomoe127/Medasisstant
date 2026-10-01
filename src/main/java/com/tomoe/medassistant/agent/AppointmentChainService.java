package com.tomoe.medassistant.agent;

public interface AppointmentChainService {
    String bookAppointmentChain(String userRequest, String model, Long userId);
}
