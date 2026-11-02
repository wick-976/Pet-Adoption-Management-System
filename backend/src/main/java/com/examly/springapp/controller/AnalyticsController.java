package com.examly.springapp.controller;

import com.examly.springapp.dto.response.AdoptionAnalyticsDto;
import com.examly.springapp.dto.response.AnalyticsResponseDto;
import com.examly.springapp.dto.response.PetAnalyticsDto;
import com.examly.springapp.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics & Reports", description = "Staff analytics, statistics, and reporting endpoints")
@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping
    @Operation(summary = "Get overall shelter analytics and metrics")
    public ResponseEntity<AnalyticsResponseDto> getOverallAnalytics() {
        AnalyticsResponseDto analytics = analyticsService.getOverallAnalytics();
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/adoptions")
    @Operation(summary = "Get adoption statistics for a selected period")
    public ResponseEntity<AdoptionAnalyticsDto> getAdoptionAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        AdoptionAnalyticsDto analytics = analyticsService.getAdoptionAnalytics(startDate, endDate);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/pets")
    @Operation(summary = "Get detailed pet analytics and species breakdown")
    public ResponseEntity<PetAnalyticsDto> getPetAnalytics() {
        PetAnalyticsDto analytics = analyticsService.getPetAnalytics();
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/export")
    @Operation(summary = "Get export-ready analytics data")
    public ResponseEntity<Map<String, Object>> getExportData() {
        Map<String, Object> data = analyticsService.getExportReadyData();
        return ResponseEntity.ok(data);
    }
}
