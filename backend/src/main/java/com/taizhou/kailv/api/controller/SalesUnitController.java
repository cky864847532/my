package com.taizhou.kailv.api.controller;

import com.taizhou.kailv.api.model.SalesUnit;
import com.taizhou.kailv.api.service.SalesUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/sales-units")
public class SalesUnitController {

    @Autowired
    private SalesUnitService salesUnitService;

    @GetMapping
    public ResponseEntity<?> list() {
        List<SalesUnit> list = salesUnitService.list();
        return ResponseEntity.ok(Map.of("total", list.size(), "items", list));
    }

    @GetMapping("/export")
    public ResponseEntity<?> exportAll() {
        return ResponseEntity.ok(Map.of("items", salesUnitService.list()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        SalesUnit s = salesUnitService.getById(id);
        if (s == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(s);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody SalesUnit body) {
        long next = salesUnitService.list().stream().mapToLong(s -> s.getId() == null ? 0L : s.getId()).max().orElse(0L) + 1L;
        body.setId(next);
        body.setCreatedAt(LocalDateTime.now());
        salesUnitService.save(body);
        return ResponseEntity.status(201).body(Map.of("id", body.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody SalesUnit body) {
        body.setId(id);
        salesUnitService.updateById(body);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        salesUnitService.removeById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> bulk(@RequestBody BulkRequest req) {
        List<SalesUnit> creates = req.getCreate() == null ? List.of() : req.getCreate();
        List<SalesUnit> updates = req.getUpdate() == null ? List.of() : req.getUpdate();
        List<Long> deleteIds = req.getDeleteIds() == null ? List.of() : req.getDeleteIds();

        long next = salesUnitService.list().stream().mapToLong(s -> s.getId() == null ? 0L : s.getId()).max().orElse(0L) + 1L;
        List<SalesUnit> preparedCreates = new ArrayList<>();
        for (SalesUnit s : creates) {
            s.setId(next++);
            s.setCreatedAt(LocalDateTime.now());
            preparedCreates.add(s);
        }

        if (!preparedCreates.isEmpty()) {
            salesUnitService.saveBatch(preparedCreates);
        }
        if (!updates.isEmpty()) {
            salesUnitService.updateBatchById(updates);
        }
        if (!deleteIds.isEmpty()) {
            salesUnitService.removeByIds(deleteIds);
        }

        return ResponseEntity.ok(Map.of(
                "created", preparedCreates.size(),
                "updated", updates.size(),
                "deleted", deleteIds.size()
        ));
    }

    public static class BulkRequest {
        private List<SalesUnit> create;
        private List<SalesUnit> update;
        private List<Long> deleteIds;

        public List<SalesUnit> getCreate() { return create; }
        public void setCreate(List<SalesUnit> create) { this.create = create; }
        public List<SalesUnit> getUpdate() { return update; }
        public void setUpdate(List<SalesUnit> update) { this.update = update; }
        public List<Long> getDeleteIds() { return deleteIds; }
        public void setDeleteIds(List<Long> deleteIds) { this.deleteIds = deleteIds; }
    }
}
