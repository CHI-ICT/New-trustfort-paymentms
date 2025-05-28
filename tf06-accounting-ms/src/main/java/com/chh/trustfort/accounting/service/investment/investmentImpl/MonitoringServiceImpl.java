package com.chh.trustfort.accounting.service.investment.investmentImpl;

import com.chh.trustfort.accounting.model.Investment;
import com.chh.trustfort.accounting.repository.InvestmentRepository;
import com.chh.trustfort.accounting.service.investment.MonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MonitoringServiceImpl implements MonitoringService {

    @Autowired
    private InvestmentRepository investmentRepository;

    @Override
    public Map<String, Object> getPortfolioMetrics() {
        List<Investment> all = investmentRepository.findAll();

        BigDecimal totalInvested = all.stream()
                .map(Investment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long maturingSoon = all.stream()
                .filter(inv -> inv.getMaturityDate().isAfter(LocalDate.now()) &&
                        inv.getMaturityDate().isBefore(LocalDate.now().plusDays(7)))
                .count();

        long underperforming = all.stream()
                .filter(inv -> inv.getExpectedReturn().compareTo(
                        inv.getAmount().multiply(inv.getAssetClass().getAverageReturnRate())) < 0)
                .count();

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalInvestments", all.size());
        metrics.put("totalInvested", totalInvested);
        metrics.put("maturingSoon", maturingSoon);
        metrics.put("underperforming", underperforming);

        return metrics;
    }
}