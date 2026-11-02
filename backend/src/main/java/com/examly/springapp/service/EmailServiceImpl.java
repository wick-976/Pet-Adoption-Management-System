package com.examly.springapp.service;

import com.examly.springapp.model.AdoptionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final boolean emailEnabled;
    private final String emailFrom;

    public EmailServiceImpl(
            @Value("${app.email.enabled:false}") boolean emailEnabled,
            @Value("${app.email.from:noreply@petadoption.org}") String emailFrom) {
        this.emailEnabled = emailEnabled;
        this.emailFrom = emailFrom;
    }

    @Override
    public void sendAdoptionConfirmation(AdoptionRequest request) {
        String to = request.getApplicantEmail();
        String subject = "Adoption Request Confirmation - " + request.getPet().getName();
        String body = String.format(
                "Hello %s,\n\n" +
                "Thank you for submitting an adoption request for %s (Species: %s, Breed: %s)!\n" +
                "Your request ID is: #%d.\n" +
                "Current Status: %s\n\n" +
                "Our shelter staff will review your application shortly and get in touch with you.\n\n" +
                "Best regards,\nPet Adoption Management System",
                request.getApplicantName(),
                request.getPet().getName(),
                request.getPet().getSpecies(),
                request.getPet().getBreed() != null ? request.getPet().getBreed() : "Unknown",
                request.getRequestId(),
                request.getStatus()
        );

        if (emailEnabled) {
            log.info("Sending adoption confirmation email to {} from {}: \nSubject: {}\n{}", to, emailFrom, subject, body);
        } else {
            log.info("Email service simulated (disabled). Notification generated for {}: \nSubject: {}\n{}", to, subject, body);
        }
    }

    @Override
    public void sendStatusUpdateNotification(AdoptionRequest request) {
        String to = request.getApplicantEmail();
        String subject = "Adoption Request Update - " + request.getPet().getName();
        String notesSection = request.getStaffNotes() != null && !request.getStaffNotes().isBlank()
                ? "\nNotes from Shelter Staff: " + request.getStaffNotes()
                : "";

        String body = String.format(
                "Hello %s,\n\n" +
                "There is an update on your adoption request for %s (Request ID: #%d).\n" +
                "New Status: %s%s\n\n" +
                "Thank you for your patience and interest in providing a loving home.\n\n" +
                "Best regards,\nPet Adoption Management System",
                request.getApplicantName(),
                request.getPet().getName(),
                request.getRequestId(),
                request.getStatus(),
                notesSection
        );

        if (emailEnabled) {
            log.info("Sending status update email to {} from {}: \nSubject: {}\n{}", to, emailFrom, subject, body);
        } else {
            log.info("Email service simulated (disabled). Status update notification generated for {}: \nSubject: {}\n{}", to, subject, body);
        }
    }
}
