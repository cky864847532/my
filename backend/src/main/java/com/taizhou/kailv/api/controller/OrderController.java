package com.taizhou.kailv.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.taizhou.kailv.api.service.OrderService;
import com.taizhou.kailv.api.model.Order;
import com.taizhou.kailv.api.model.OrderItem;
import com.taizhou.kailv.api.mapper.OrderItemMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        // 简化实现：保存订单主体和订单项（真实场景应做事务、库存校验）
        Order order = new Order();
        Long customerId = parseLongSafe(body.get("customerId"));
        if (customerId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "invalid customerId"));
        }
        order.setCustomerId(customerId);
        order.setStatus("已创建");
        order.setCreatedAt(LocalDateTime.now());
        orderService.save(order);

        List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
        double total = 0;
        if (items != null) {
            for (Map<String, Object> it : items) {
                Long pid = parseLongSafe(it.get("productId"));
                Integer qty = parseIntSafe(it.get("qty"));
                Double price = parseDoubleSafe(it.get("price"));
                if (pid == null || qty == null || price == null) {
                    return ResponseEntity.badRequest().body(Map.of("error", "invalid order item"));
                }
                OrderItem oi = new OrderItem();
                oi.setOrderId(order.getId());
                oi.setProductId(pid);
                oi.setQty(qty);
                oi.setPrice(price);
                orderItemMapper.insert(oi);
                total += price * qty;
            }
        }
        order.setTotalAmount(total);
        orderService.updateById(order);
        return ResponseEntity.status(201).body(Map.of("orderId", order.getId()));
    }

    private Long parseLongSafe(Object o) {
        if (o == null) return null;
        if (o instanceof Number) return ((Number) o).longValue();
        if (o instanceof String) {
            try { return Long.valueOf(((String) o).trim()); } catch (Exception e) { return null; }
        }
        if (o instanceof Boolean) return ((Boolean) o) ? 1L : 0L;
        try { return Long.valueOf(o.toString().trim()); } catch (Exception e) { return null; }
    }

    private Integer parseIntSafe(Object o) {
        if (o == null) return null;
        if (o instanceof Number) return ((Number) o).intValue();
        if (o instanceof String) {
            try { return Integer.valueOf(((String) o).trim()); } catch (Exception e) { return null; }
        }
        if (o instanceof Boolean) return ((Boolean) o) ? 1 : 0;
        try { return Integer.valueOf(o.toString().trim()); } catch (Exception e) { return null; }
    }

    private Double parseDoubleSafe(Object o) {
        if (o == null) return null;
        if (o instanceof Number) return ((Number) o).doubleValue();
        if (o instanceof String) {
            try { return Double.valueOf(((String) o).trim()); } catch (Exception e) { return null; }
        }
        if (o instanceof Boolean) return ((Boolean) o) ? 1.0 : 0.0;
        try { return Double.valueOf(o.toString().trim()); } catch (Exception e) { return null; }
    }

    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(orderService.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Order o = orderService.getById(id);
        if (o == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(o);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Order o = orderService.getById(id);
        if (o == null) return ResponseEntity.notFound().build();
        if (body.containsKey("customerId")) {
            Long cid = parseLongSafe(body.get("customerId"));
            if (cid != null) {
                o.setCustomerId(cid);
            }
        }
        if (body.containsKey("status")) {
            o.setStatus(body.get("status").toString());
        }
        orderService.updateById(o);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Order o = orderService.getById(id);
        if (o == null) return ResponseEntity.notFound().build();
        o.setStatus(body.getOrDefault("status", o.getStatus()).toString());
        orderService.updateById(o);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean removed = orderService.removeById(id);
        if (!removed) return ResponseEntity.notFound().build();
        // 订单项级联删除（简单处理）
        orderItemMapper.delete(new QueryWrapper<OrderItem>().lambda().eq(OrderItem::getOrderId, id));
        return ResponseEntity.noContent().build();
    }
}
