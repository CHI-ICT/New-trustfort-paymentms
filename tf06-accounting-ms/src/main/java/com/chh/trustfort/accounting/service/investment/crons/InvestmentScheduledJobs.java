package com.chh.trustfort.accounting.service.investment.crons;

import com.chh.trustfort.accounting.repository.InvestmentRepository;
import com.chh.trustfort.accounting.service.investment.MaturityService;
import com.chh.trustfort.accounting.service.investment.RiskAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InvestmentScheduledJobs {

    @Autowired
    private MaturityService maturityService;

    @Autowired
    private RiskAssessmentService riskAssessmentService;

    @Autowired
    private InvestmentRepository investmentRepository;

    @Scheduled(cron = "0 0 8 * * ?") // Every day at 8AM
    public void runMaturityCheck() {
        maturityService.checkMaturingInvestments();
    }

    @Scheduled(cron = "0 0 9 * * MON") // Every Monday at 9AM
    public void runWeeklyRiskAssessment() {
        investmentRepository.findAll().forEach(investment -> {
            riskAssessmentService.assess(investment.getId());
        });
    }
}

