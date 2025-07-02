package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatementIntegrityService {

    private final IncomeStatementService incomeService;
    private final EquityStatementService equityService;
    private final BalanceSheetService balanceService;
    private final CashFlowStatementService cashFlowService;

    public List<IntegrityCheckResult> validateAllStatements(BalanceSheetFilterRequest filter) {
        StatementFilterDTO converted = toStatementFilter(filter);
        List<IntegrityCheckResult> results = new ArrayList<>();

        results.add(validateIncomeVsEquity(converted));
        results.add(validateCashFlowVsBalanceSheet(converted));
        results.add(validateBalanceSheetEquation(filter));

        return results;
    }

    public IntegrityCheckResult validateIncomeVsEquity(StatementFilterDTO filter) {
        IncomeStatementResponse income = incomeService.generateIncomeStatement(filter);
        EquityStatementResponse equity = equityService.generateStatement(filter);

        boolean match = income.getNetIncome().compareTo(equity.getRetainedEarnings()) == 0;

        return new IntegrityCheckResult(
                "R1",
                "Net Income should match Retained Earnings movement",
                match,
                match
                        ? "✔ Values match"
                        : "❌ Mismatch: Net Income = " + income.getNetIncome() + ", Retained Earnings = " + equity.getRetainedEarnings()
        );
    }

    public IntegrityCheckResult validateBalanceSheetEquation(BalanceSheetFilterRequest filter) {
        BalanceSheetResponse bs = balanceService.generateBalanceSheet(filter);

        boolean match = bs.getTotalAssets().compareTo(bs.getTotalLiabilities().add(bs.getTotalEquity())) == 0;

        return new IntegrityCheckResult(
                "R2",
                "Assets = Liabilities + Equity",
                match,
                match
                        ? "✔ Equation holds"
                        : "❌ Equation broken: Assets = " + bs.getTotalAssets() + ", L+E = " + bs.getTotalLiabilities().add(bs.getTotalEquity())
        );
    }

    public IntegrityCheckResult validateCashFlowVsBalanceSheet(StatementFilterDTO filter) {
        CashFlowStatementDTO cash = cashFlowService.generateCashFlowStatement(filter);
        BalanceSheetFilterRequest balanceSheetFilter = toBalanceSheetFilter(filter);
        BalanceSheetResponse bs = balanceService.generateBalanceSheet(balanceSheetFilter);

        boolean match = cash.getClosingCashBalance().compareTo(bs.getCashAtEnd()) == 0;

        return new IntegrityCheckResult(
                "R3",
                "Closing Cash in Cash Flow = Ending Cash in Balance Sheet",
                match,
                match
                        ? "✔ Values match"
                        : "❌ Mismatch: Cash Flow = " + cash.getClosingCashBalance() + ", Balance Sheet = " + bs.getCashAtEnd()
        );
    }

    private StatementFilterDTO toStatementFilter(BalanceSheetFilterRequest filter) {
        StatementFilterDTO dto = new StatementFilterDTO();
        dto.setStartDate(filter.getStartDate());
        dto.setEndDate(filter.getEndDate());
        dto.setDepartment(filter.getDepartment());
        dto.setBusinessUnit(filter.getBusinessUnit());
        return dto;
    }

    private BalanceSheetFilterRequest toBalanceSheetFilter(StatementFilterDTO dto) {
        BalanceSheetFilterRequest filter = new BalanceSheetFilterRequest();
        filter.setStartDate(dto.getStartDate());
        filter.setEndDate(dto.getEndDate());
        filter.setDepartment(dto.getDepartment());
        filter.setBusinessUnit(dto.getBusinessUnit());
        return filter;
    }
}
