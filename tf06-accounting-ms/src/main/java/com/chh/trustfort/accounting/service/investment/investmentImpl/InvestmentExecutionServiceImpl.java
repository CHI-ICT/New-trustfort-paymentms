package com.chh.trustfort.accounting.service.investment.investmentImpl;

import com.chh.trustfort.accounting.dto.investment.InvestmentExecutionRequestDTO;
import com.chh.trustfort.accounting.model.Investment;
import com.chh.trustfort.accounting.model.InvestmentExecutionLog;
import com.chh.trustfort.accounting.repository.InvestmentExecutionLogRepository;
import com.chh.trustfort.accounting.repository.InvestmentRepository;
import com.chh.trustfort.accounting.service.investment.InvestmentExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InvestmentExecutionServiceImpl implements InvestmentExecutionService {

    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private InvestmentExecutionLogRepository executionLogRepository;

    @Override
    public void executeInvestment(InvestmentExecutionRequestDTO dto) {
        Investment investment = investmentRepository.findById(dto.investmentId)
                .orElseThrow(() -> new IllegalArgumentException("Investment not found"));

        InvestmentExecutionLog log = new InvestmentExecutionLog();
        log.setInvestmentId(dto.investmentId);
        log.setAmount(investment.getAmount());
        log.setExecutedBy(dto.executor);
        log.setExecutedAt(LocalDateTime.now());
        log.setRemarks("Investment executed successfully");

        executionLogRepository.save(log);

        System.out.println("Investment ID " + dto.investmentId + " executed by " + dto.executor);
    }
}