package com.taizhou.kailv.api.service;

import java.util.List;
import java.util.Map;

public interface StatsService {
    List<Map<String, Object>> sales(String startDate, String endDate, String groupBy);
    List<Map<String, Object>> topProducts(int limit);
    List<Map<String, Object>> topSalesUnits(int limit);
    List<Map<String, Object>> topCustomers(int limit);
    Map<String, Object> summary();
    List<Map<String, Object>> inventoryByProduct();
}
