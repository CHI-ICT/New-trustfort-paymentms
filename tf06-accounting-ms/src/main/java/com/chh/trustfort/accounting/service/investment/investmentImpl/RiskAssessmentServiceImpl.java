package com.chh.trustfort.accounting.service.investment.investmentImpl;

import com.chh.trustfort.accounting.enums.RiskAssessmentLevel;
import com.chh.trustfort.accounting.model.Investment;
import com.chh.trustfort.accounting.model.RiskAssessmentResult;
import com.chh.trustfort.accounting.repository.InvestmentRepository;
import com.chh.trustfort.accounting.repository.RiskAssessmentResultRepository;
import com.chh.trustfort.accounting.service.investment.RiskAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class RiskAssessmentServiceImpl implements RiskAssessmentService {

    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private RiskAssessmentResultRepository riskAssessmentResultRepository;

    @Override
    public RiskAssessmentResult assess(Long investmentId) {
        Investment investment = investmentRepository.findById(investmentId)
                .orElseThrow(() -> new IllegalArgumentException("Investment not found"));

        BigDecimal benchmark = investment.getAssetClass().getAverageReturnRate();
        BigDecimal expected = investment.getExpectedReturn();
        BigDecimal actual = investment.getAmount().multiply(benchmark);

        String riskLevel;
        String reason;

        if (expected.compareTo(actual) < 0) {
            riskLevel = "HIGH";
            reason = "Expected return below benchmark";
        } else if (expected.compareTo(actual) == 0) {
            riskLevel = "MEDIUM";
            reason = "Expected return matches benchmark";
        } else {
            riskLevel = "LOW";
            reason = "Expected return exceeds benchmark";
        }

        RiskAssessmentResult result = new RiskAssessmentResult();
        result.setInvestmentId(investmentId);
        result.setRiskLevel(RiskAssessmentLevel.LOW);
        result.setReason(reason);
        result.setEvaluatedAt(LocalDateTime.now());

        riskAssessmentResultRepository.save(result);
        return result;
    }
}
