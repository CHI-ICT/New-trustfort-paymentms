package com.chh.trustfort.accounting.controller.investment;

import com.chh.trustfort.accounting.dto.investment.AssetClassRequestDTO;
import com.chh.trustfort.accounting.model.AssetClass;
import com.chh.trustfort.accounting.service.investment.AssetClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/asset-classes")
public class AssetClassController {

    @Autowired
    private AssetClassService service;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody AssetClassRequestDTO assetClassRequestDTO) {
        return ResponseEntity.ok(service.createAssetClass(assetClassRequestDTO));
    }

    @GetMapping
    public ResponseEntity<List<AssetClass>> getAll() {
        return ResponseEntity.ok(service.getAllAssetClasses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetClass> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AssetClass> update(@PathVariable Long id, @RequestBody AssetClass assetClass) {
        return ResponseEntity.ok(service.updateAssetClass(id, assetClass));
    }
}
