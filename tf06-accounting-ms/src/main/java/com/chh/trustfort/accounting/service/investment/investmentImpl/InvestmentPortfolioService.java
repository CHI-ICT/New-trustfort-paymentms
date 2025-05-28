package com.chh.trustfort.accounting.service.investment.investmentImpl;

import com.chh.trustfort.accounting.dto.InvestmentCalculationResultDTO;
import com.chh.trustfort.accounting.dto.investment.InvestmentRequestDTO;
import com.chh.trustfort.accounting.enums.InsuranceProductType;
import com.chh.trustfort.accounting.model.AssetClass;
import com.chh.trustfort.accounting.model.Institution;
import com.chh.trustfort.accounting.model.Investment;
import com.chh.trustfort.accounting.model.InvestmentRule;
import com.chh.trustfort.accounting.repository.AssetClassRepository;
import com.chh.trustfort.accounting.repository.InstitutionRepository;
import com.chh.trustfort.accounting.repository.InvestmentRepository;
import com.chh.trustfort.accounting.repository.InvestmentRuleRepository;
import com.chh.trustfort.accounting.service.investment.InvestmentAuditService;
import com.chh.trustfort.accounting.service.investment.InvestmentCalculationService;
import com.chh.trustfort.accounting.service.investment.ReturnCalculationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class InvestmentPortfolioService {
    @Autowired
    private InvestmentRuleRepository ruleRepo;
    @Autowired
    private InvestmentCalculationService calculationService;
    @Autowired
    private InvestmentRepository repo;
    @Autowired
    private AssetClassRepository assetRepo;
    @Autowired
    private InstitutionRepository instRepo;
    @Autowired
    private ReturnCalculationService returnCalc;
    @Autowired
    private InvestmentAuditService audit;

    @Transactional
    public Investment createInvestment(InvestmentRequestDTO dto) {
        AssetClass asset = assetRepo.findById(dto.getAssetClassId())
                .orElseThrow(() -> new IllegalArgumentException("Asset class not found"));
        Institution inst = instRepo.findById(dto.getInstitutionId())
                .orElseThrow(() -> new IllegalArgumentException("Institution not found"));

        validateInsuranceRules(dto, asset);

        // Calculate tenor in partial years (e.g., 1.5 years)
        long daysBetween = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getMaturityDate());
        if (daysBetween <= 0) {
            throw new IllegalArgumentException("Maturity date must be after start date.");
        }

        BigDecimal tenorYears = BigDecimal.valueOf(daysBetween)
                .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);

        // Use calculated tenorYears here
        InvestmentCalculationResultDTO calc = calculationService.calculate(
                dto.getAssetClassId(),
                dto.getAmount(),
                tenorYears
        );

        Investment investment = new Investment();
        investment.setAmount(dto.getAmount());
        investment.setAssetClass(asset);
        investment.setStartDate(dto.getStartDate());
        investment.setMaturityDate(dto.getMaturityDate());
        investment.setType(dto.getType());
        investment.setInstitution(inst);
        investment.setInsuranceProductType(dto.getInsuranceProductType());
        investment.setParticipating(dto.isParticipating());
        investment.setRoi(calc.getRoi());
        investment.setTenorYears(tenorYears);
        investment.setInterest(calc.getInterest());
        investment.setDividends(calc.getDividends());

        log.info("Created Investment: ROI={}, Interest={}, Dividends={}, AssetClass={}, Tenor={} years",
                calc.getRoi(), calc.getInterest(), calc.getDividends(),
                asset.getName(), tenorYears);

        return repo.save(investment);
    }

    public Investment rollOverInvestment(Long investmentId, String user, String role) throws AccessDeniedException {
        if (!"INVESTMENT_EXECUTIVE".equals(role)) {
            throw new AccessDeniedException("Unauthorized: Role '" + role + "' is not permitted to roll over investments.");
        }

        Investment original = repo.findById(investmentId)
                .orElseThrow(() -> new IllegalArgumentException("Original investment not found"));

        if (!original.getMaturityDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Investment has not matured yet.");
        }

        Investment newInv = new Investment();
        newInv.setReference(generateInvestmentReference());
        newInv.setAmount(original.getAmount());
        newInv.setAssetClass(original.getAssetClass());
        newInv.setInstitution(original.getInstitution());
        newInv.setStartDate(LocalDate.now());
        newInv.setMaturityDate(LocalDate.now().plusYears(1));
        newInv.setExpectedReturn(returnCalc.calculateExpectedReturn(
                newInv.getAmount(),
                newInv.getAssetClass().getAverageReturnRate(),
                newInv.getStartDate(),
                newInv.getMaturityDate()));
        newInv.setInsuranceProductType(original.getInsuranceProductType());
        newInv.setParticipating(original.isParticipating());
        newInv.setCreatedBy(user);
        newInv.setCreatedAt(LocalDateTime.now());

        original.setRolledOver(true);
        repo.save(original);

        Investment saved = repo.save(newInv);
        audit.logAction(original.getId(), "ROLLED_OVER", user, "New Investment Reference: " + saved.getReference());

        return saved;
    }

    private void validateInsuranceRules(InvestmentRequestDTO dto, AssetClass asset) {
        if (dto.getInsuranceProductType() == InsuranceProductType.LIFE
                && asset.getRiskLevel().equalsIgnoreCase("High")) {
            throw new IllegalArgumentException("LIFE insurance cannot invest in high-risk assets.");
        }

        long tenorYears = ChronoUnit.YEARS.between(dto.getStartDate(), dto.getMaturityDate());
        if (dto.getInsuranceProductType() == InsuranceProductType.LIFE && tenorYears < 10) {
            throw new IllegalArgumentException("Tenor must be at least 10 years for LIFE insurance.");
        }
    }

    public void validateInvestmentRequest(InvestmentRequestDTO dto, AssetClass asset) {
        List<InvestmentRule> rules = ruleRepo.findApplicable(dto.getInsuranceProductType(), asset.getId());

        for (InvestmentRule rule : rules) {
            if (!rule.isAllowHighRisk() && asset.getRiskLevel().equalsIgnoreCase("High")) {
                throw new IllegalArgumentException("High-risk not allowed for " + dto.getInsuranceProductType());
            }

            long tenor = ChronoUnit.YEARS.between(dto.getStartDate(), dto.getMaturityDate());
            if (rule.getMinTenorYears() != null && tenor < rule.getMinTenorYears()) {
                throw new IllegalArgumentException("Minimum tenor required: " + rule.getMinTenorYears());
            }

            if (rule.getMaxAmount() != null && dto.getAmount().compareTo(rule.getMaxAmount()) > 0) {
                throw new IllegalArgumentException("Amount exceeds max for " + asset.getName());
            }
        }
    }

    private BigDecimal calculateTotalExpectedReturn(BigDecimal amount, BigDecimal annualRate,
                                                    LocalDate start, LocalDate end, boolean isParticipating) {
        BigDecimal baseReturn = returnCalc.calculateExpectedReturn(amount, annualRate, start, end);
        if (isParticipating) {
            return baseReturn.add(baseReturn.multiply(BigDecimal.valueOf(0.05))); // 5% bonus for participation
        }
        return baseReturn;
    }

    private String generateInvestmentReference() {
        return "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}