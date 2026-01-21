package com.taizhou.kailv.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.taizhou.kailv.api.service.InventoryService;
import com.taizhou.kailv.api.model.InventoryRecord;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(inventoryService.list());
    }

    @GetMapping("/export")
    public ResponseEntity<?> exportAll() {
        return ResponseEntity.ok(Map.of("items", inventoryService.list()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        InventoryRecord record = inventoryService.getById(id);
        if (record == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(record);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody InventoryRecord body) {
        long next = inventoryService.list().stream().mapToLong(r -> r.getId() == null ? 0L : r.getId()).max().orElse(0L) + 1L;
        body.setId(next);
        body.setCreatedAt(body.getCreatedAt() != null ? body.getCreatedAt() : LocalDateTime.now());
        inventoryService.save(body);
        return ResponseEntity.status(201).body(Map.of("id", body.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody InventoryRecord body) {
        InventoryRecord existing = inventoryService.getById(id);
        if (existing == null) return ResponseEntity.notFound().build();
        body.setId(id);
        if (body.getCreatedAt() == null) {
            body.setCreatedAt(existing.getCreatedAt());
        }
        inventoryService.updateById(body);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean removed = inventoryService.removeById(id);
        if (!removed) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> bulk(@RequestBody BulkRequest req) {
        List<InventoryRecord> creates = req.getCreate() == null ? List.of() : req.getCreate();
        List<InventoryRecord> updates = req.getUpdate() == null ? List.of() : req.getUpdate();
        List<Long> deleteIds = req.getDeleteIds() == null ? List.of() : req.getDeleteIds();

        long next = inventoryService.list().stream().mapToLong(r -> r.getId() == null ? 0L : r.getId()).max().orElse(0L) + 1L;
        List<InventoryRecord> preparedCreates = new ArrayList<>();
        for (InventoryRecord r : creates) {
            r.setId(next++);
            r.setCreatedAt(r.getCreatedAt() != null ? r.getCreatedAt() : LocalDateTime.now());
            preparedCreates.add(r);
        }

        if (!updates.isEmpty()) {
            List<Long> updateIds = updates.stream()
                .map(InventoryRecord::getId)
                .filter(Objects::nonNull)
                .toList();
            Map<Long, InventoryRecord> existingMap = inventoryService.listByIds(updateIds)
                    .stream()
                    .collect(Collectors.toMap(InventoryRecord::getId, r -> r));
            for (InventoryRecord r : updates) {
                InventoryRecord match = existingMap.get(r.getId());
                if (r.getCreatedAt() == null && match != null) r.setCreatedAt(match.getCreatedAt());
            }
        }

        if (!preparedCreates.isEmpty()) {
            inventoryService.saveBatch(preparedCreates);
        }
        if (!updates.isEmpty()) {
            inventoryService.updateBatchById(updates);
        }
        if (!deleteIds.isEmpty()) {
            inventoryService.removeByIds(deleteIds);
        }

        return ResponseEntity.ok(Map.of(
                "created", preparedCreates.size(),
                "updated", updates.size(),
                "deleted", deleteIds.size()
        ));
    }

    public static class BulkRequest {
        private List<InventoryRecord> create;
        private List<InventoryRecord> update;
        private List<Long> deleteIds;

        public List<InventoryRecord> getCreate() { return create; }
        public void setCreate(List<InventoryRecord> create) { this.create = create; }
        public List<InventoryRecord> getUpdate() { return update; }
        public void setUpdate(List<InventoryRecord> update) { this.update = update; }
        public List<Long> getDeleteIds() { return deleteIds; }
        public void setDeleteIds(List<Long> deleteIds) { this.deleteIds = deleteIds; }
    }
}
