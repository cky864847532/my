package com.taizhou.kailv.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.taizhou.kailv.api.model.Product;
import com.taizhou.kailv.api.model.Sale;
import com.taizhou.kailv.api.model.SalesUnit;
import com.taizhou.kailv.api.model.InventoryRecord;
import com.taizhou.kailv.api.model.Purchase;
import com.taizhou.kailv.api.service.ProductService;
import com.taizhou.kailv.api.service.SaleService;
import com.taizhou.kailv.api.service.SalesUnitService;
import com.taizhou.kailv.api.service.StatsService;
import com.taizhou.kailv.api.service.InventoryService;
import com.taizhou.kailv.api.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class StatsServiceImpl implements StatsService {
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Autowired
    private SaleService saleService;
    @Autowired
    private ProductService productService;
    @Autowired
    private SalesUnitService salesUnitService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private PurchaseService purchaseService;

    @Override
    public List<Map<String, Object>> sales(String startDate, String endDate, String groupBy) {
        List<Sale> sales = loadSales(startDate, endDate);
        if ("product".equalsIgnoreCase(groupBy)) {
            return aggregateByProduct(sales, Integer.MAX_VALUE);
        }
        if ("customer".equalsIgnoreCase(groupBy) || "salesUnit".equalsIgnoreCase(groupBy)) {
            return aggregateBySalesUnit(sales, Integer.MAX_VALUE);
        }
        return aggregateByPeriod(sales, "month".equalsIgnoreCase(groupBy));
    }

    @Override
    public List<Map<String, Object>> topProducts(int limit) {
        return aggregateByProduct(loadSales(null, null), limit);
    }

    @Override
    public List<Map<String, Object>> topSalesUnits(int limit) {
        return aggregateBySalesUnit(loadSales(null, null), limit);
    }

    @Override
    public List<Map<String, Object>> topCustomers(int limit) {
        return topSalesUnits(limit);
    }

    @Override
    public Map<String, Object> summary() {
        LocalDate now = LocalDate.now();
        LocalDateTime monthStart = now.withDayOfMonth(1).atStartOfDay();
        LocalDateTime monthEnd = now.withDayOfMonth(now.lengthOfMonth()).atTime(LocalTime.MAX);

        List<Sale> monthSales = saleService.list(new LambdaQueryWrapper<Sale>()
                .ge(Sale::getCreatedAt, monthStart)
                .le(Sale::getCreatedAt, monthEnd));
        List<Purchase> monthPurchases = purchaseService.list(new LambdaQueryWrapper<Purchase>()
                .ge(Purchase::getCreatedAt, monthStart)
                .le(Purchase::getCreatedAt, monthEnd));

        BigDecimal salesTotal = monthSales.stream().map(this::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        long salesQty = monthSales.stream().mapToLong(s -> s.getQuantity() == null ? 0 : s.getQuantity()).sum();
        BigDecimal purchaseTotal = monthPurchases.stream()
                .map(p -> {
                    BigDecimal price = p.getPurchasePrice() == null ? BigDecimal.ZERO : BigDecimal.valueOf(p.getPurchasePrice());
                    int qty = p.getQuantity() == null ? 0 : p.getQuantity();
                    return price.multiply(BigDecimal.valueOf(qty));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 简单目标：当月销售目标为上月销售额的 110%，若无历史则给定基准值 100000
        BigDecimal target;
        List<Sale> lastMonthSales = saleService.list(new LambdaQueryWrapper<Sale>()
                .ge(Sale::getCreatedAt, monthStart.minusMonths(1))
                .le(Sale::getCreatedAt, monthEnd.minusMonths(1)));
        BigDecimal lastMonthTotal = lastMonthSales.stream().map(this::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (lastMonthTotal.compareTo(BigDecimal.ZERO) > 0) {
            target = lastMonthTotal.multiply(BigDecimal.valueOf(1.1)).setScale(2, BigDecimal.ROUND_HALF_UP);
        } else {
            target = BigDecimal.valueOf(100000);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("monthSalesTotal", salesTotal);
        map.put("monthSalesQty", salesQty);
        map.put("monthPurchaseTotal", purchaseTotal);
        map.put("monthSalesTarget", target);
        map.put("targetProgress", target.compareTo(BigDecimal.ZERO) == 0 ? 0 : salesTotal.divide(target, 4, BigDecimal.ROUND_HALF_UP));
        map.put("inventorySkus", inventoryService.count());
        map.put("productCount", productService.count());
        map.put("customerCount", salesUnitService.count());
        return map;
    }

    @Override
    public List<Map<String, Object>> inventoryByProduct() {
        List<InventoryRecord> records = inventoryService.list();
        Map<Long, Integer> qtyMap = new LinkedHashMap<>();
        for (InventoryRecord r : records) {
            if (r.getProductId() == null) continue;
            int qty = r.getQuantity() == null ? 0 : r.getQuantity();
            qtyMap.merge(r.getProductId(), qty, Integer::sum);
        }

        Map<Long, String> nameMap = productService.listByIds(qtyMap.keySet()).stream()
                .collect(Collectors.toMap(Product::getId, Product::getName));

        return qtyMap.entrySet().stream()
                .map(e -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", e.getKey());
                    row.put("name", nameMap.getOrDefault(e.getKey(), "未知产品"));
                    row.put("quantity", e.getValue());
                    return row;
                })
                .sorted(Comparator.comparingLong(o -> -((Number) o.get("quantity")).longValue()))
                .toList();
    }

    private List<Sale> loadSales(String startDate, String endDate) {
        LocalDate start = parseDate(startDate);
        LocalDate end = parseDate(endDate);

        LambdaQueryWrapper<Sale> wrapper = new LambdaQueryWrapper<>();
        if (start != null) {
            wrapper.ge(Sale::getCreatedAt, start.atStartOfDay());
        }
        if (end != null) {
            wrapper.le(Sale::getCreatedAt, end.atTime(LocalTime.MAX));
        }
        return saleService.list(wrapper);
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (Exception ignored) {
            return null;
        }
    }

    private BigDecimal lineTotal(Sale sale) {
        BigDecimal price = sale.getSalePrice() == null ? BigDecimal.ZERO : BigDecimal.valueOf(sale.getSalePrice());
        int qty = sale.getQuantity() == null ? 0 : sale.getQuantity();
        return price.multiply(BigDecimal.valueOf(qty));
    }

    private List<Map<String, Object>> aggregateByPeriod(List<Sale> sales, boolean byMonth) {
        Map<String, StatBucket> buckets = new TreeMap<>();
        for (Sale sale : sales) {
            LocalDateTime created = sale.getCreatedAt();
            if (created == null) {
                continue;
            }
            String key = byMonth ? created.format(MONTH_FORMATTER) : created.toLocalDate().toString();
            StatBucket bucket = buckets.computeIfAbsent(key, k -> StatBucket.period(k));
            bucket.add(lineTotal(sale), sale.getQuantity());
        }

        return buckets.values().stream()
                .map(bucket -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("period", bucket.getLabel());
                    row.put("total", bucket.getTotal());
                    row.put("qty", bucket.getQuantity());
                    return row;
                })
                .toList();
    }

    private List<Map<String, Object>> aggregateByProduct(List<Sale> sales, int limit) {
        Set<Long> productIds = sales.stream()
                .map(Sale::getProductId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, String> productNameMap = productService.listByIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Product::getName));

        Map<Long, StatBucket> buckets = new LinkedHashMap<>();
        for (Sale sale : sales) {
            Long productId = sale.getProductId();
            if (productId == null) {
                continue;
            }
            StatBucket bucket = buckets.computeIfAbsent(productId,
                    id -> StatBucket.grouped(id, productNameMap.getOrDefault(id, "未知产品")));
            bucket.add(lineTotal(sale), sale.getQuantity());
        }

        return buckets.values().stream()
                .sorted(Comparator.comparing(StatBucket::getTotal).reversed())
                .limit(Math.max(limit, 0))
                .map(bucket -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", bucket.getId());
                    row.put("name", bucket.getLabel());
                    row.put("total", bucket.getTotal());
                    row.put("qty", bucket.getQuantity());
                    return row;
                })
                .toList();
    }

    private List<Map<String, Object>> aggregateBySalesUnit(List<Sale> sales, int limit) {
        Set<Long> salesUnitIds = sales.stream()
                .map(Sale::getSalesUnitId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, String> salesUnitNameMap = salesUnitService.listByIds(salesUnitIds).stream()
                .collect(Collectors.toMap(SalesUnit::getId, SalesUnit::getName));

        Map<Long, StatBucket> buckets = new LinkedHashMap<>();
        for (Sale sale : sales) {
            Long salesUnitId = sale.getSalesUnitId();
            if (salesUnitId == null) {
                continue;
            }
            StatBucket bucket = buckets.computeIfAbsent(salesUnitId,
                    id -> StatBucket.grouped(id, salesUnitNameMap.getOrDefault(id, "未知客户")));
            bucket.add(lineTotal(sale), sale.getQuantity());
        }

        return buckets.values().stream()
                .sorted(Comparator.comparing(StatBucket::getTotal).reversed())
                .limit(Math.max(limit, 0))
                .map(bucket -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", bucket.getId());
                    row.put("name", bucket.getLabel());
                    row.put("total", bucket.getTotal());
                    row.put("qty", bucket.getQuantity());
                    return row;
                })
                .toList();
    }

    private static class StatBucket {
        private final Long id;
        private final String label;
        private BigDecimal total = BigDecimal.ZERO;
        private long quantity;

        private StatBucket(Long id, String label) {
            this.id = id;
            this.label = label;
        }

        static StatBucket period(String label) {
            return new StatBucket(null, label);
        }

        static StatBucket grouped(Long id, String label) {
            return new StatBucket(id, label);
        }

        void add(BigDecimal amount, Integer qty) {
            int safeQty = qty == null ? 0 : qty;
            this.total = this.total.add(amount == null ? BigDecimal.ZERO : amount);
            this.quantity += safeQty;
        }

        Long getId() {
            return id;
        }

        String getLabel() {
            return label;
        }

        BigDecimal getTotal() {
            return total;
        }

        long getQuantity() {
            return quantity;
        }
    }
}
