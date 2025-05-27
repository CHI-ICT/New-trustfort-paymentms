package com.chh.trustfort.payment.controller.facility;

import com.chh.trustfort.payment.dto.CreditLineRequestDto;
import com.chh.trustfort.payment.dto.CreditLineResponseDto;
import com.chh.trustfort.payment.service.facility.CreditLineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credit-lines")
public class CreditLineController {

    private final CreditLineService creditLineService;

    public CreditLineController(CreditLineService creditLineService) {
        this.creditLineService = creditLineService;
    }

    @PostMapping
    public ResponseEntity<CreditLineResponseDto> create(@RequestBody CreditLineRequestDto dto) {
        return ResponseEntity.ok(creditLineService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<CreditLineResponseDto>> getAll() {
        return ResponseEntity.ok(creditLineService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreditLineResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(creditLineService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreditLineResponseDto> update(@PathVariable Long id, @RequestBody CreditLineRequestDto dto) {
        return ResponseEntity.ok(creditLineService.update(id, dto));
    }
}

