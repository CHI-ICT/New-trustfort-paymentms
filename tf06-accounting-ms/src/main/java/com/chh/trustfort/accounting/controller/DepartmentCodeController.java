package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.model.DepartmentCode;
import com.chh.trustfort.accounting.payload.DepartmentCodeRequestDTO;
import com.chh.trustfort.accounting.service.DepartmentCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/department-codes")
@RequiredArgsConstructor
public class DepartmentCodeController {

    private final DepartmentCodeService service;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody DepartmentCodeRequestDTO dto) {
        try {
            DepartmentCode created = service.create(dto);
            return ResponseEntity.ok(created);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<DepartmentCode>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{code}")
    public ResponseEntity<DepartmentCode> getByCode(@PathVariable String code) {
        return service.findByCode(code).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{code}")
    public ResponseEntity<DepartmentCode> update(@PathVariable String code, @RequestBody DepartmentCodeRequestDTO dto) {
        return ResponseEntity.ok(service.update(code, dto));
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<DepartmentCode>> getDeleted() {
        return ResponseEntity.ok(service.findAllDeleted());
    }

    @PatchMapping()
    public ResponseEntity<?> softDelete(@RequestBody String code) {
        return ResponseEntity.ok( service.delete(code));
    }

    @PutMapping("/{code}/restore")
    public ResponseEntity<DepartmentCode> restore(@PathVariable String code) {
        return ResponseEntity.ok(service.restore(code));
    }

}