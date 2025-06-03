package com.chh.trustfort.accounting.service.investment.investmentImpl;

import com.chh.trustfort.accounting.dto.InvestmentCalculationResultDTO;
import com.chh.trustfort.accounting.dto.investment.InvestmentRequestDTO;
import com.chh.trustfort.accounting.dto.investment.InvestmentResponseDTO;
import com.chh.trustfort.accounting.model.*;
import com.chh.trustfort.accounting.repository.*;
import com.chh.trustfort.accounting.service.investment.InvestmentCalculationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
public class InvestmentPortfolioService {
    @Autowired private InstitutionRepository institutionRepository;
    @Autowired private InvestmentCalculationService calculationService;
    @Autowired private InvestmentFactory investmentFactory;
    @Autowired private InvestmentRepository investmentRepository;
    @Autowired private MoneyMarketCalculationService moneyMarketCalcService;
    @Autowired private EurobondCalculationService eurobondCalcService;
    @Autowired private MoneyMarketInvestmentRepository moneyMarketRepo;
    @Autowired private EurobondInvestmentRepository eurobondRepo;
    @Autowired private CorporateAndGovtBondInvestmentRepository corporateBondRepo;
    @Autowired private QuotedEquityInvestmentRepository quotedEquityRepo;
    @Autowired private UnquotedEquityInvestmentRepository unquotedEquityRepo;
    @Autowired private CorporateAndGovtBondCalculationService corporateAndGovtBondCalculationService;
    @Autowired private CommercialPaperCalculationService commercialPaperCalculationService;
    @Autowired private TreasuryBillCalculationService treasuryBillCalculationService;

    @Transactional
    public InvestmentResponseDTO createInvestment(InvestmentRequestDTO dto) {
        long days = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getMaturityDate());
        if (days <= 0) throw new IllegalArgumentException("Maturity date must be after start date.");

        Institution institution = institutionRepository.findById(dto.getInstitutionId())
                .orElseThrow(() -> new IllegalArgumentException("Institution not found"));

        InvestmentCalculationResultDTO calc = calculationService.calculate(dto.getSubtype(), dto.getAmount(), dto.getTenor());

        InvestmentVehicle vehicle = investmentFactory.create(dto, institution, calc, dto.getTenor());
        InvestmentVehicle savedVehicle = persistByType(vehicle);

        switch (vehicle.getInvestmentType()) {
            case MONEY_MARKET:
                moneyMarketCalcService.calculate((MoneyMarketInvestment) vehicle, BigDecimal.ZERO);
                break;
            case FIXED_INCOME:
                switch (vehicle.getSubtype()) {
                    case EUROBOND:
                        eurobondCalcService.calculate((EurobondInvestment) vehicle);
                        break;
                    case CORPORATE_AND_GOVT_BOND:
                        corporateAndGovtBondCalculationService.calculate((CorporateAndGovtBondInvestment) vehicle);
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported fixed income subtype: " + vehicle.getSubtype());
                }
                break;
            case COMMERCIAL_PAPER:
                commercialPaperCalculationService.calculate((CommercialPaperInvestment) vehicle);
                break;
            case TREASURY_BILL:
                treasuryBillCalculationService.calculate((TreasuryBillInvestment) vehicle);
                break;
            default:
                throw new IllegalArgumentException("Unsupported investment type: " + vehicle.getInvestmentType());
        }

        Investment investment = Investment.builder()
                .reference("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .asset(savedVehicle)
                .amount(dto.getAmount())
                .startDate(dto.getStartDate())
                .maturityDate(dto.getMaturityDate())
                .createdAt(LocalDateTime.now())
                .createdBy("system")
                .isParticipating(dto.isParticipating())
                .insuranceProductType(dto.getInsuranceProductType())
                .roi(calc.getRoi())
                .build();

        Investment saved = investmentRepository.save(investment);

        return InvestmentResponseDTO.builder()
                .id(saved.getId())
                .reference(saved.getReference())
                .amount(saved.getAmount())
                .currency(dto.getCurrency())
                .type(dto.getType())
                .subtype(dto.getSubtype())
                .roi(saved.getRoi())
                .startDate(saved.getStartDate())
                .maturityDate(saved.getMaturityDate())
                .expectedReturn(calc.getExpectedReturn())
                .institutionName(institution.getName())
                .status("CREATED")
                .build();
    }


    private InvestmentVehicle persistByType(InvestmentVehicle investment) {
        if (investment instanceof MoneyMarketInvestment) return moneyMarketRepo.save((MoneyMarketInvestment) investment);
        if (investment instanceof EurobondInvestment) return eurobondRepo.save((EurobondInvestment) investment);
        if (investment instanceof CorporateAndGovtBondInvestment) return corporateBondRepo.save((CorporateAndGovtBondInvestment) investment);
        if (investment instanceof QuotedEquityInvestment) return quotedEquityRepo.save((QuotedEquityInvestment) investment);
        if (investment instanceof UnQuotedEquityInvestment) return unquotedEquityRepo.save((UnQuotedEquityInvestment) investment);
        throw new IllegalArgumentException("Unsupported investment vehicle type");
    }

}
