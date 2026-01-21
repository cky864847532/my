package com.taizhou.kailv.api.controller;

import com.taizhou.kailv.api.model.Sale;
import com.taizhou.kailv.api.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/sales")
public class SalesController {

    @Autowired
    private SaleService saleService;

    @GetMapping
    public ResponseEntity<?> list() {
        List<Sale> list = saleService.list();
        return ResponseEntity.ok(Map.of("total", list.size(), "items", list));
    }

    @GetMapping("/export")
    public ResponseEntity<?> exportAll() {
        return ResponseEntity.ok(Map.of("items", saleService.list()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Sale s = saleService.getById(id);
        if (s == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(s);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Sale body) {
        long next = saleService.list().stream().mapToLong(s -> s.getId() == null ? 0L : s.getId()).max().orElse(0L) + 1L;
        body.setId(next);
        if (body.getCreatedAt() == null) {
            body.setCreatedAt(LocalDateTime.now());
        }
        saleService.save(body);
        return ResponseEntity.status(201).body(Map.of("id", body.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Sale body) {
        body.setId(id);
        saleService.updateById(body);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        saleService.removeById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> bulk(@RequestBody BulkRequest req) {
        List<Sale> creates = req.getCreate() == null ? List.of() : req.getCreate();
        List<Sale> updates = req.getUpdate() == null ? List.of() : req.getUpdate();
        List<Long> deleteIds = req.getDeleteIds() == null ? List.of() : req.getDeleteIds();

        long next = saleService.list().stream().mapToLong(s -> s.getId() == null ? 0L : s.getId()).max().orElse(0L) + 1L;
        List<Sale> preparedCreates = new ArrayList<>();
        for (Sale s : creates) {
            s.setId(next++);
            if (s.getCreatedAt() == null) {
                s.setCreatedAt(LocalDateTime.now());
            }
            preparedCreates.add(s);
        }

        if (!preparedCreates.isEmpty()) {
            saleService.saveBatch(preparedCreates);
        }
        if (!updates.isEmpty()) {
            saleService.updateBatchById(updates);
        }
        if (!deleteIds.isEmpty()) {
            saleService.removeByIds(deleteIds);
        }

        return ResponseEntity.ok(Map.of(
                "created", preparedCreates.size(),
                "updated", updates.size(),
                "deleted", deleteIds.size()
        ));
    }

    public static class BulkRequest {
        private List<Sale> create;
        private List<Sale> update;
        private List<Long> deleteIds;

        public List<Sale> getCreate() { return create; }
        public void setCreate(List<Sale> create) { this.create = create; }
        public List<Sale> getUpdate() { return update; }
        public void setUpdate(List<Sale> update) { this.update = update; }
        public List<Long> getDeleteIds() { return deleteIds; }
        public void setDeleteIds(List<Long> deleteIds) { this.deleteIds = deleteIds; }
    }
}
