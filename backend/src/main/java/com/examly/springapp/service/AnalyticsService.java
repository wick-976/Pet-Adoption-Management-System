package com.examly.springapp.service;

import com.examly.springapp.dto.response.AdoptionAnalyticsDto;
import com.examly.springapp.dto.response.AnalyticsResponseDto;
import com.examly.springapp.dto.response.PetAnalyticsDto;

import java.time.LocalDateTime;
import java.util.Map;

public interface AnalyticsService {

    AnalyticsResponseDto getOverallAnalytics();

    AdoptionAnalyticsDto getAdoptionAnalytics(LocalDateTime startDate, LocalDateTime endDate);

    PetAnalyticsDto getPetAnalytics();

    Map<String, Object> getExportReadyData();
}
