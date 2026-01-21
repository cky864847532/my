package com.taizhou.kailv.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.taizhou.kailv.api.service.CustomerService;
import com.taizhou.kailv.api.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(required = false) Integer page,
                                  @RequestParam(required = false) Integer size,
                                  @RequestParam(required = false) String name) {
        // 简单返回全部（可扩展为分页）
        List<Customer> list = customerService.list();
        return ResponseEntity.ok(Map.of("total", list.size(), "items", list));
    }

    @GetMapping("/export")
    public ResponseEntity<?> exportAll() {
        return ResponseEntity.ok(Map.of("items", customerService.list()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Customer c = customerService.getById(id);
        if (c == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(c);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Customer body) {
        // set id as max(id)+1
        long next = customerService.list().stream().mapToLong(c -> c.getId() == null ? 0L : c.getId()).max().orElse(0L) + 1L;
        body.setId(next);
        body.setCreatedAt(LocalDateTime.now());
        customerService.save(body);
        return ResponseEntity.status(201).body(Map.of("id", body.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Customer body) {
        body.setId(id);
        customerService.updateById(body);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        customerService.removeById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> bulk(@RequestBody BulkRequest req) {
        List<Customer> creates = req.getCreate() == null ? List.of() : req.getCreate();
        List<Customer> updates = req.getUpdate() == null ? List.of() : req.getUpdate();
        List<Long> deleteIds = req.getDeleteIds() == null ? List.of() : req.getDeleteIds();

        long next = customerService.list().stream().mapToLong(c -> c.getId() == null ? 0L : c.getId()).max().orElse(0L) + 1L;
        List<Customer> preparedCreates = new ArrayList<>();
        for (Customer c : creates) {
            c.setId(next++);
            c.setCreatedAt(LocalDateTime.now());
            preparedCreates.add(c);
        }

        if (!preparedCreates.isEmpty()) {
            customerService.saveBatch(preparedCreates);
        }
        if (!updates.isEmpty()) {
            customerService.updateBatchById(updates);
        }
        if (!deleteIds.isEmpty()) {
            customerService.removeByIds(deleteIds);
        }

        return ResponseEntity.ok(Map.of(
                "created", preparedCreates.size(),
                "updated", updates.size(),
                "deleted", deleteIds.size()
        ));
    }

    public static class BulkRequest {
        private List<Customer> create;
        private List<Customer> update;
        private List<Long> deleteIds;

        public List<Customer> getCreate() {
            return create;
        }

        public void setCreate(List<Customer> create) {
            this.create = create;
        }

        public List<Customer> getUpdate() {
            return update;
        }

        public void setUpdate(List<Customer> update) {
            this.update = update;
        }

        public List<Long> getDeleteIds() {
            return deleteIds;
        }

        public void setDeleteIds(List<Long> deleteIds) {
            this.deleteIds = deleteIds;
        }
    }
}
