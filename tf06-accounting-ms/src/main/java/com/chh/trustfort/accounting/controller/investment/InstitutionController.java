package com.chh.trustfort.accounting.controller.investment;

import com.chh.trustfort.accounting.dto.investment.InstitutionRequestDTO;
import com.chh.trustfort.accounting.dto.investment.InstitutionResponseDTO;
import com.chh.trustfort.accounting.model.Institution;
import com.chh.trustfort.accounting.service.investment.InstitutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/institutions")
public class InstitutionController {

    @Autowired
    private InstitutionService service;

    @PostMapping
    public ResponseEntity<InstitutionResponseDTO> create(@RequestBody InstitutionRequestDTO dto) {
        return ResponseEntity.ok(service.createInstitution(dto));
    }

    @GetMapping
    public ResponseEntity<List<Institution>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Institution> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Institution updatedFields) {
        return service.getById(id)
                .map(existing -> {
                    updatedFields.setName(existing.getName()); // Prevent name update
                    return ResponseEntity.ok(service.updateInstitution(id, updatedFields));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}