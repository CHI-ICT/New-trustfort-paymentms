package com.chh.trustfort.accounting.controller.investment;

import com.chh.trustfort.accounting.dto.investment.InvestmentRuleRequestDTO;
import com.chh.trustfort.accounting.dto.investment.InvestmentRuleResponseDTO;
import com.chh.trustfort.accounting.service.investment.InvestmentRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investment-rules")
public class InvestmentRuleController {

    @Autowired
    private InvestmentRuleService ruleService;

    @PostMapping
    public ResponseEntity<InvestmentRuleResponseDTO> create(@RequestBody InvestmentRuleRequestDTO dto) {
        return ResponseEntity.ok(ruleService.createRule(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvestmentRuleResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ruleService.getRuleById(id));
    }

    @GetMapping("/{assetClassId}")
    public ResponseEntity<List<InvestmentRuleResponseDTO>> getByAsset(@PathVariable Long assetClassId) {
        return ResponseEntity.ok(ruleService.getRulesByAssetClass(assetClassId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<InvestmentRuleResponseDTO>> getActive() {
        return ResponseEntity.ok(ruleService.getActiveRules());
    }

    @GetMapping
    public ResponseEntity<List<InvestmentRuleResponseDTO>> getAll() {
        return ResponseEntity.ok(ruleService.getAllRules());
    }

    @PutMapping("/{id}")
    public ResponseEntity<InvestmentRuleResponseDTO> update(@PathVariable Long id, @RequestBody InvestmentRuleRequestDTO dto) {
        return ResponseEntity.ok(ruleService.updateRule(id, dto));
    }
}


