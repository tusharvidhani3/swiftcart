package com.swiftcart.swiftcart.features.dashboard;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    @PreAuthorize("hasRole('SELLER')")
    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(@RequestParam(defaultValue = "WEEK") TimeSpan span) {
        DashboardResponse dashboardResponse = dashboardService.getDashboard(span);
        return ResponseEntity.ok(dashboardResponse);
    }
}
