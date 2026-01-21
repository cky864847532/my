package com.taizhou.kailv.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.taizhou.kailv.api.service.StatsService;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    @Autowired
    private StatsService statsService;

    @GetMapping("/sales")
    public ResponseEntity<?> sales(@RequestParam(required = false) String startDate,
                                   @RequestParam(required = false) String endDate,
                                   @RequestParam(required = false) String groupBy) {
        List<Map<String, Object>> rows = statsService.sales(startDate, endDate, groupBy);
        return ResponseEntity.ok(Map.of("total", rows.size(), "items", rows));
    }


    @GetMapping("/products/top")
    public ResponseEntity<?> topProducts(@RequestParam(required = false, defaultValue = "10") Integer limit) {
        List<Map<String, Object>> rows = statsService.topProducts(limit);
        return ResponseEntity.ok(Map.of("total", rows.size(), "items", rows));
    }

    @GetMapping("/sales-units/top")
    public ResponseEntity<?> topSalesUnits(@RequestParam(required = false, defaultValue = "10") Integer limit) {
        List<Map<String, Object>> rows = statsService.topSalesUnits(limit);
        return ResponseEntity.ok(Map.of("total", rows.size(), "items", rows));
    }

    @GetMapping("/customers/top")
    public ResponseEntity<?> topCustomers(@RequestParam(required = false, defaultValue = "10") Integer limit) {
        List<Map<String, Object>> rows = statsService.topCustomers(limit);
        return ResponseEntity.ok(Map.of("total", rows.size(), "items", rows));
    }

    @GetMapping("/summary")
    public ResponseEntity<?> summary() {
        Map<String, Object> data = statsService.summary();
        return ResponseEntity.ok(data);
    }

    @GetMapping("/inventory")
    public ResponseEntity<?> inventory() {
        List<Map<String, Object>> rows = statsService.inventoryByProduct();
        return ResponseEntity.ok(Map.of("total", rows.size(), "items", rows));
    }
}
