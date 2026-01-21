package com.taizhou.kailv.api.controller;

import com.taizhou.kailv.api.model.Purchase;
import com.taizhou.kailv.api.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/purchases")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @GetMapping
    public ResponseEntity<?> list() {
        List<Purchase> list = purchaseService.list();
        return ResponseEntity.ok(Map.of("total", list.size(), "items", list));
    }

    @GetMapping("/export")
    public ResponseEntity<?> exportAll() {
        return ResponseEntity.ok(Map.of("items", purchaseService.list()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Purchase p = purchaseService.getById(id);
        if (p == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(p);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Purchase body) {
        long next = purchaseService.list().stream().mapToLong(p -> p.getId() == null ? 0L : p.getId()).max().orElse(0L) + 1L;
        body.setId(next);
        if (body.getCreatedAt() == null) {
            body.setCreatedAt(LocalDateTime.now());
        }
        purchaseService.save(body);
        return ResponseEntity.status(201).body(Map.of("id", body.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Purchase body) {
        body.setId(id);
        purchaseService.updateById(body);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        purchaseService.removeById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> bulk(@RequestBody BulkRequest req) {
        List<Purchase> creates = req.getCreate() == null ? List.of() : req.getCreate();
        List<Purchase> updates = req.getUpdate() == null ? List.of() : req.getUpdate();
        List<Long> deleteIds = req.getDeleteIds() == null ? List.of() : req.getDeleteIds();

        long next = purchaseService.list().stream().mapToLong(p -> p.getId() == null ? 0L : p.getId()).max().orElse(0L) + 1L;
        List<Purchase> preparedCreates = new ArrayList<>();
        for (Purchase p : creates) {
            p.setId(next++);
            if (p.getCreatedAt() == null) {
                p.setCreatedAt(LocalDateTime.now());
            }
            preparedCreates.add(p);
        }

        if (!preparedCreates.isEmpty()) {
            purchaseService.saveBatch(preparedCreates);
        }
        if (!updates.isEmpty()) {
            purchaseService.updateBatchById(updates);
        }
        if (!deleteIds.isEmpty()) {
            purchaseService.removeByIds(deleteIds);
        }

        return ResponseEntity.ok(Map.of(
                "created", preparedCreates.size(),
                "updated", updates.size(),
                "deleted", deleteIds.size()
        ));
    }

    public static class BulkRequest {
        private List<Purchase> create;
        private List<Purchase> update;
        private List<Long> deleteIds;

        public List<Purchase> getCreate() { return create; }
        public void setCreate(List<Purchase> create) { this.create = create; }
        public List<Purchase> getUpdate() { return update; }
        public void setUpdate(List<Purchase> update) { this.update = update; }
        public List<Long> getDeleteIds() { return deleteIds; }
        public void setDeleteIds(List<Long> deleteIds) { this.deleteIds = deleteIds; }
    }
}
