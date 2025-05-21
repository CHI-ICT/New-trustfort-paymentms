package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.model.AccountCategory;
import com.chh.trustfort.accounting.payload.AccountCategoryRequest;
import com.chh.trustfort.accounting.service.AccountCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/account-categories")
public class AccountCategoryController {

    @Autowired
    private AccountCategoryService service;

    @PostMapping
    public ResponseEntity<AccountCategory> create(@RequestBody AccountCategoryRequest req) {
        return ResponseEntity.ok(service.createCategory(req));
    }

    @GetMapping
    public ResponseEntity<List<AccountCategory>> getAll() {
        return ResponseEntity.ok(service.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountCategory> getById(@PathVariable Long id) {
        return service.getCategoryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountCategory> update(@PathVariable Long id,
                                                  @RequestBody AccountCategoryRequest req) {
        try {
            return ResponseEntity.ok(service.updateCategory(id, req));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        try {
//            service.deleteCategory(id);
//            return ResponseEntity.noContent().build();
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
}
