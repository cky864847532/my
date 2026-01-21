package com.taizhou.kailv.api.controller;

import com.taizhou.kailv.api.model.Supplier;
import com.taizhou.kailv.api.service.SupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    @GetMapping
    public ResponseEntity<?> list() {
        List<Supplier> list = supplierService.list();
        return ResponseEntity.ok(Map.of("total", list.size(), "items", list));
    }

    @GetMapping("/export")
    public ResponseEntity<?> exportAll() {
        return ResponseEntity.ok(Map.of("items", supplierService.list()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Supplier s = supplierService.getById(id);
        if (s == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(s);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Supplier body) {
        long next = supplierService.list().stream().mapToLong(s -> s.getId() == null ? 0L : s.getId()).max().orElse(0L) + 1L;
        body.setId(next);
        body.setCreatedAt(LocalDateTime.now());
        supplierService.save(body);
        return ResponseEntity.status(201).body(Map.of("id", body.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Supplier body) {
        body.setId(id);
        supplierService.updateById(body);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        supplierService.removeById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> bulk(@RequestBody BulkRequest req) {
        List<Supplier> creates = req.getCreate() == null ? List.of() : req.getCreate();
        List<Supplier> updates = req.getUpdate() == null ? List.of() : req.getUpdate();
        List<Long> deleteIds = req.getDeleteIds() == null ? List.of() : req.getDeleteIds();

        long next = supplierService.list().stream().mapToLong(s -> s.getId() == null ? 0L : s.getId()).max().orElse(0L) + 1L;
        List<Supplier> preparedCreates = new ArrayList<>();
        for (Supplier s : creates) {
            s.setId(next++);
            s.setCreatedAt(LocalDateTime.now());
            preparedCreates.add(s);
        }

        if (!preparedCreates.isEmpty()) {
            supplierService.saveBatch(preparedCreates);
        }
        if (!updates.isEmpty()) {
            supplierService.updateBatchById(updates);
        }
        if (!deleteIds.isEmpty()) {
            supplierService.removeByIds(deleteIds);
        }

        return ResponseEntity.ok(Map.of(
                "created", preparedCreates.size(),
                "updated", updates.size(),
                "deleted", deleteIds.size()
        ));
    }

    public static class BulkRequest {
        private List<Supplier> create;
        private List<Supplier> update;
        private List<Long> deleteIds;

        public List<Supplier> getCreate() { return create; }
        public void setCreate(List<Supplier> create) { this.create = create; }
        public List<Supplier> getUpdate() { return update; }
        public void setUpdate(List<Supplier> update) { this.update = update; }
        public List<Long> getDeleteIds() { return deleteIds; }
        public void setDeleteIds(List<Long> deleteIds) { this.deleteIds = deleteIds; }
    }
}
