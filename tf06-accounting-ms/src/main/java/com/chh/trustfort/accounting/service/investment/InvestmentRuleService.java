package com.chh.trustfort.accounting.service.investment;

import com.chh.trustfort.accounting.dto.investment.InvestmentRuleRequestDTO;
import com.chh.trustfort.accounting.dto.investment.InvestmentRuleResponseDTO;

import java.util.List;

public interface InvestmentRuleService {
    InvestmentRuleResponseDTO createRule(InvestmentRuleRequestDTO dto);
    List<InvestmentRuleResponseDTO> getAllRules();
    InvestmentRuleResponseDTO updateRule(Long id, InvestmentRuleRequestDTO dto);
    InvestmentRuleResponseDTO getRuleById(Long id);
    List<InvestmentRuleResponseDTO> getRulesByAssetClass(Long assetClassId);
    List<InvestmentRuleResponseDTO> getActiveRules();
}
