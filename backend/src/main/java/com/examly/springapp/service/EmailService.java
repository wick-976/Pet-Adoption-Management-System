package com.examly.springapp.service;

import com.examly.springapp.model.AdoptionRequest;

public interface EmailService {

    void sendAdoptionConfirmation(AdoptionRequest request);

    void sendStatusUpdateNotification(AdoptionRequest request);
}
