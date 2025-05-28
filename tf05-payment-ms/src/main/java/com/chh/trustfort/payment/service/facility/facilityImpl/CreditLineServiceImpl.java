package com.chh.trustfort.payment.service.facility.facilityImpl;
import com.chh.trustfort.payment.dto.CreditLineRequestDto;
import com.chh.trustfort.payment.dto.CreditLineResponseDto;
import com.chh.trustfort.payment.enums.CreditStatus;
import com.chh.trustfort.payment.model.facility.CreditLine;
import com.chh.trustfort.payment.repository.CreditLineRepository;
import com.chh.trustfort.payment.service.facility.CreditLineService;
import com.chh.trustfort.payment.service.facility.IRuleEngineService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CreditLineServiceImpl implements CreditLineService {
    private final CreditLineRepository creditLineRepo;
    private final IRuleEngineService ruleEngineService;

    public CreditLineServiceImpl(CreditLineRepository creditLineRepo, IRuleEngineService ruleEngineService) {
        this.creditLineRepo = creditLineRepo;
        this.ruleEngineService = ruleEngineService;
    }

    @Override
    public CreditLineResponseDto create(CreditLineRequestDto dto) {
        CreditLine credit = new CreditLine();
        credit.setUserId(dto.getUserId());
        credit.setAmount(dto.getAmount());
        credit.setRepaidAmount(BigDecimal.ZERO);
        credit.setStatus(CreditStatus.PENDING);
        credit.setReason(dto.getReason());
        credit.setRequestedAt(LocalDateTime.now());

        CreditLine saved = creditLineRepo.save(credit);
        ruleEngineService.applyApprovalRules(credit);
        return mapToDto(saved);
    }

    @Override
    public List<CreditLineResponseDto> getAll() {
        return creditLineRepo.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    public CreditLineResponseDto getById(Long id) {
        return creditLineRepo.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new RuntimeException("Credit line not found"));
    }

    @Override
    public CreditLineResponseDto update(Long id, CreditLineRequestDto dto) {
        CreditLine credit = creditLineRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Credit line not found"));

        credit.setAmount(dto.getAmount());
        credit.setReason(dto.getReason());
        creditLineRepo.save(credit);

        return mapToDto(credit);
    }

    private CreditLineResponseDto mapToDto(CreditLine credit) {
        CreditLineResponseDto dto = new CreditLineResponseDto();
        dto.setId(credit.getId());
        dto.setUserId(credit.getUserId());
        dto.setAmount(credit.getAmount());
        dto.setRepaidAmount(credit.getRepaidAmount());
        dto.setStatus(credit.getStatus());
        dto.setReason(credit.getReason());
        dto.setRequestedAt(credit.getRequestedAt());
        return dto;
    }
}
