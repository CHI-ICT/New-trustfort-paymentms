package com.chh.trustfort.accounting.service.investment.investmentImpl;

import com.chh.trustfort.accounting.dto.investment.InvestmentRuleRequestDTO;
import com.chh.trustfort.accounting.dto.investment.InvestmentRuleResponseDTO;
import com.chh.trustfort.accounting.model.AssetClass;
import com.chh.trustfort.accounting.model.InvestmentRule;
import com.chh.trustfort.accounting.repository.AssetClassRepository;
import com.chh.trustfort.accounting.repository.InvestmentRuleRepository;
import com.chh.trustfort.accounting.service.investment.InvestmentRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvestmentRuleServiceImpl implements InvestmentRuleService {

    @Autowired
    private InvestmentRuleRepository ruleRepo;

    @Autowired
    private AssetClassRepository assetClassRepo;

    @Override
    public InvestmentRuleResponseDTO updateRule(Long id, InvestmentRuleRequestDTO dto) {
        InvestmentRule existing = ruleRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Rule not found"));
        existing.setDisabled(true);
        existing.setDeleted(true);
        existing.setVersion(+1);
        ruleRepo.save(existing);

        InvestmentRuleRequestDTO newDto = new InvestmentRuleRequestDTO();
        newDto.insuranceType = dto.insuranceType;
        newDto.assetClassId = dto.assetClassId;
        newDto.minTenorYears = dto.minTenorYears;
        newDto.maxAmount = dto.maxAmount;
        newDto.allowHighRisk = dto.allowHighRisk;
        newDto.createdBy = dto.createdBy;

        return createRule(newDto);
    }

    @Override
    public InvestmentRuleResponseDTO createRule(InvestmentRuleRequestDTO dto) {
        List<InvestmentRule> existingRules = ruleRepo.findAll().stream()
                .filter(r -> r.getInsuranceType() == dto.insuranceType &&
                        ((r.getAssetClass() == null && dto.assetClassId == null) ||
                                (r.getAssetClass() != null && r.getAssetClass().getId().equals(dto.assetClassId))) &&
                        !r.isDeleted())
                .collect(Collectors.toList());

        for (InvestmentRule existing : existingRules) {
            existing.setDisabled(true);
            existing.setDeleted(true);
            ruleRepo.save(existing);
        }

        InvestmentRule rule = new InvestmentRule();
        rule.setInsuranceType(dto.insuranceType);
        rule.setMinTenorYears(dto.minTenorYears);
        rule.setMaxAmount(dto.maxAmount);
        rule.setAllowHighRisk(dto.allowHighRisk);
        rule.setCreatedBy(dto.createdBy);
        rule.setCreatedAt(LocalDateTime.now());
        rule.setVersion(existingRules.stream().mapToInt(r -> r.getVersion() == null ? 1 : r.getVersion()).max().orElse(0) + 1);

        if (dto.assetClassId != null) {
            AssetClass asset = assetClassRepo.findById(dto.assetClassId)
                    .orElseThrow(() -> new IllegalArgumentException("Asset class not found"));
            rule.setAssetClass(asset);
        }

        InvestmentRule saved = ruleRepo.save(rule);

        InvestmentRuleResponseDTO response = new InvestmentRuleResponseDTO();
        response.id = saved.getId();
        response.insuranceType = saved.getInsuranceType();
        response.assetClassName = saved.getAssetClass() != null ? saved.getAssetClass().getName() : null;
        response.minTenorYears = saved.getMinTenorYears();
        response.maxAmount = saved.getMaxAmount();
        response.allowHighRisk = saved.isAllowHighRisk();
        response.createdBy = saved.getCreatedBy();
        response.createdAt = saved.getCreatedAt();

        return response;
    }

    @Override
    public InvestmentRuleResponseDTO getRuleById(Long id) {
        InvestmentRule rule = ruleRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rule not found"));

        InvestmentRuleResponseDTO dto = new InvestmentRuleResponseDTO();
        dto.id = rule.getId();
        dto.insuranceType = rule.getInsuranceType();
        dto.assetClassName = rule.getAssetClass() != null ? rule.getAssetClass().getName() : null;
        dto.minTenorYears = rule.getMinTenorYears();
        dto.maxAmount = rule.getMaxAmount();
        dto.allowHighRisk = rule.isAllowHighRisk();
        dto.createdBy = rule.getCreatedBy();
        dto.createdAt = rule.getCreatedAt();
        return dto;
    }

    @Override
    public List<InvestmentRuleResponseDTO> getRulesByAssetClass(Long assetClassId) {
        return ruleRepo.findAll().stream()
                .filter(rule -> rule.getAssetClass() != null && rule.getAssetClass().getId().equals(assetClassId))
                .map(rule -> {
                    InvestmentRuleResponseDTO dto = new InvestmentRuleResponseDTO();
                    dto.id = rule.getId();
                    dto.insuranceType = rule.getInsuranceType();
                    dto.assetClassName = rule.getAssetClass().getName();
                    dto.minTenorYears = rule.getMinTenorYears();
                    dto.maxAmount = rule.getMaxAmount();
                    dto.allowHighRisk = rule.isAllowHighRisk();
                    dto.createdBy = rule.getCreatedBy();
                    dto.createdAt = rule.getCreatedAt();
                    return dto;
                }).collect(Collectors.toList());
    }

    @Override
    public List<InvestmentRuleResponseDTO> getActiveRules() {
        return ruleRepo.findAll().stream()
                .filter(rule -> !rule.isDeleted())
                .map(rule -> {
                    InvestmentRuleResponseDTO dto = new InvestmentRuleResponseDTO();
                    dto.id = rule.getId();
                    dto.insuranceType = rule.getInsuranceType();
                    dto.assetClassName = rule.getAssetClass() != null ? rule.getAssetClass().getName() : null;
                    dto.minTenorYears = rule.getMinTenorYears();
                    dto.maxAmount = rule.getMaxAmount();
                    dto.allowHighRisk = rule.isAllowHighRisk();
                    dto.createdBy = rule.getCreatedBy();
                    dto.createdAt = rule.getCreatedAt();
                    return dto;
                }).collect(Collectors.toList());
    }

    @Override
    public List<InvestmentRuleResponseDTO> getAllRules() {
        return ruleRepo.findAll().stream().map(rule -> {
            InvestmentRuleResponseDTO dto = new InvestmentRuleResponseDTO();
            dto.id = rule.getId();
            dto.insuranceType = rule.getInsuranceType();
            dto.assetClassName = rule.getAssetClass() != null ? rule.getAssetClass().getName() : null;
            dto.minTenorYears = rule.getMinTenorYears();
            dto.maxAmount = rule.getMaxAmount();
            dto.allowHighRisk = rule.isAllowHighRisk();
            dto.createdBy = rule.getCreatedBy();
            dto.createdAt = rule.getCreatedAt();
            return dto;
        }).collect(Collectors.toList());
    }
}
