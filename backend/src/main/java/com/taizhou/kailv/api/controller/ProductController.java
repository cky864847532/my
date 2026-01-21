package com.taizhou.kailv.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.taizhou.kailv.api.service.ProductService;
import com.taizhou.kailv.api.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(productService.list());
    }

    @GetMapping("/export")
    public ResponseEntity<?> exportAll() {
        return ResponseEntity.ok(Map.of("items", productService.list()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Product p = productService.getById(id);
        if (p == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(p);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Product body) {
        normalize(body);
        if (productService.existsByNameAndSpec(body.getName(), body.getSpec(), null)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "产品名称与规格已存在");
        }
        long next = productService.list().stream().mapToLong(p -> p.getId() == null ? 0L : p.getId()).max().orElse(0L) + 1L;
        body.setId(next);
        body.setCreatedAt(LocalDateTime.now());
        productService.save(body);
        return ResponseEntity.status(201).body(Map.of("id", body.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Product body) {
        normalize(body);
        if (productService.existsByNameAndSpec(body.getName(), body.getSpec(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "产品名称与规格已存在");
        }
        body.setId(id);
        productService.updateById(body);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> bulk(@RequestBody BulkRequest req) {
        List<Product> creates = req.getCreate() == null ? List.of() : req.getCreate();
        List<Product> updates = req.getUpdate() == null ? List.of() : req.getUpdate();
        List<Long> deleteIds = req.getDeleteIds() == null ? List.of() : req.getDeleteIds();

        // assign incremental ids for creates
        long next = productService.list().stream().mapToLong(p -> p.getId() == null ? 0L : p.getId()).max().orElse(0L) + 1L;
        List<Product> preparedCreates = new ArrayList<>();
        for (Product p : creates) {
            normalize(p);
            if (productService.existsByNameAndSpec(p.getName(), p.getSpec(), null)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "产品名称与规格已存在: " + p.getName() + " / " + p.getSpec());
            }
            p.setId(next++);
            p.setCreatedAt(LocalDateTime.now());
            preparedCreates.add(p);
        }

        if (!preparedCreates.isEmpty()) {
            productService.saveBatch(preparedCreates);
        }
        if (!updates.isEmpty()) {
            updates.forEach(this::normalize);
            productService.updateBatchById(updates);
        }
        if (!deleteIds.isEmpty()) {
            productService.removeByIds(deleteIds);
        }
        return ResponseEntity.ok(Map.of(
                "created", preparedCreates.size(),
                "updated", updates.size(),
                "deleted", deleteIds.size()
        ));
    }

    private void normalize(Product body) {
        if (body.getName() != null) {
            body.setName(body.getName().trim());
        }
        if (body.getSpec() == null) {
            body.setSpec("");
        } else {
            body.setSpec(body.getSpec().trim());
        }
    }

    public static class BulkRequest {
        private List<Product> create;
        private List<Product> update;
        private List<Long> deleteIds;

        public List<Product> getCreate() {
            return create;
        }

        public void setCreate(List<Product> create) {
            this.create = create;
        }

        public List<Product> getUpdate() {
            return update;
        }

        public void setUpdate(List<Product> update) {
            this.update = update;
        }

        public List<Long> getDeleteIds() {
            return deleteIds;
        }

        public void setDeleteIds(List<Long> deleteIds) {
            this.deleteIds = deleteIds;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        productService.removeById(id);
        return ResponseEntity.noContent().build();
    }
}
