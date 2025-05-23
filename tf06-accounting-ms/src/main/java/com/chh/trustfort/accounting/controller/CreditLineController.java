package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.dto.CreditLineResponseDto;
import com.chh.trustfort.accounting.payload.CreditLineRequestDto;
import com.chh.trustfort.accounting.service.CreditLineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/credit-lines")
public class CreditLineController {

    private final CreditLineService service;

    public CreditLineController(CreditLineService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CreditLineResponseDto> create(@RequestBody CreditLineRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<CreditLineResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreditLineResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreditLineResponseDto> update(
            @PathVariable Long id,
            @RequestBody CreditLineRequestDto dto
    ) {
        return ResponseEntity.ok(service.update(id, dto));
    }
}

