package com.chh.trustfort.accounting.service.investment.investmentImpl;

import com.chh.trustfort.accounting.dto.InvestmentCalculationResultDTO;
import com.chh.trustfort.accounting.dto.investment.InvestmentRequestDTO;
import com.chh.trustfort.accounting.model.*;
import com.chh.trustfort.accounting.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class InvestmentFactory {

    @Autowired
    private MoneyMarketInvestmentRepository moneyMarketRepo;
    @Autowired
    private EurobondInvestmentRepository eurobondRepo;
    @Autowired
    private CorporateAndGovtBondInvestmentRepository corporateBondRepo;
    @Autowired
    private QuotedEquityInvestmentRepository quotedEquityRepo;
    @Autowired
    private UnquotedEquityInvestmentRepository unquotedEquityRepo;

    public InvestmentVehicle create(InvestmentRequestDTO dto, Institution institution, InvestmentCalculationResultDTO calc, Long tenorYears) {
        switch (dto.getSubtype()) {
            case NAIRA:
            case DOLLAR:
                MoneyMarketInvestment mm = new MoneyMarketInvestment();
                mm.setTransactionDate(LocalDate.now());
                mm.setNatureOfTransaction("New Investment");
                mm.setStartingPrincipal(dto.getAmount());
                mm.setAddition(BigDecimal.ZERO);
                mm.setLiquidation(BigDecimal.ZERO);
                mm.setNewPrincipal(dto.getAmount());
                mm.setRatePA(dto.getRatePA());
                mm.setValueDate(dto.getStartDate());
                mm.setEndOfPeriodDate(dto.getMaturityDate());
                mm.setAdjustedInterest(BigDecimal.ZERO);
                mm.setActualInterest(BigDecimal.ZERO);
                mm.setWithHoldingTax(BigDecimal.ZERO);
                mm.setClosingPrincipal(dto.getAmount());
                mm.setInstitution(institution);
                return populateShared(mm, dto, institution, calc, tenorYears);

            case EUROBOND:
                EurobondInvestment eurobond = new EurobondInvestment();
                eurobond.setSettlementDate(LocalDate.now());
                eurobond.setFaceValue(dto.getAmount());
                return populateShared(eurobond, dto, institution, calc, tenorYears);

            case CORPORATE_AND_GOVT_BOND:
                CorporateAndGovtBondInvestment corp = new CorporateAndGovtBondInvestment();
                corp.setBondSeries("Series A");
                corp.setFaceValue(dto.getAmount());
                return populateShared(corp, dto, institution, calc, tenorYears);

            case QUOTED_EQUITY:
                QuotedEquityInvestment quoted = new QuotedEquityInvestment();
                quoted.setNameOfStock(dto.getName());
                quoted.setMarketPrice(dto.getAmount());
                return populateShared(quoted, dto, institution, calc, tenorYears);

            case UNQUOTED_EQUITY:
                UnQuotedEquityInvestment unquoted = new UnQuotedEquityInvestment();
                unquoted.setUnquotedStock(dto.getName());
                unquoted.setAmount(dto.getAmount());
                return populateShared(unquoted, dto, institution, calc, tenorYears);

            default:
                throw new IllegalArgumentException("Unsupported subtype: " + dto.getSubtype());
        }
    }

    private <T extends InvestmentVehicle> T populateShared(
            T investment,
            InvestmentRequestDTO dto,
            Institution institution,
            InvestmentCalculationResultDTO calc,
            Long tenorYears
    ) {
        investment.setCurrency(dto.getCurrency());
        investment.setPrincipal(dto.getAmount());
        investment.setAmount(dto.getAmount());
        investment.setInterest(calc.getInterest());
        investment.setNetInterest(calc.getExpectedReturn());
        investment.setExpectedReturn(calc.getExpectedReturn());
        investment.setTenor(tenorYears);
        investment.setIssueDate(dto.getStartDate());
        investment.setStartDate(dto.getStartDate());
        investment.setMaturityDate(dto.getMaturityDate());
        investment.setSubtype(dto.getSubtype());
        investment.setInvestmentType(dto.getType());
        investment.setInstitution(institution);
        investment.setInsuranceProductType(dto.getInsuranceProductType());
        investment.setCreatedAt(LocalDateTime.now());
        investment.setCreatedBy("system");
        return investment;
    }

}