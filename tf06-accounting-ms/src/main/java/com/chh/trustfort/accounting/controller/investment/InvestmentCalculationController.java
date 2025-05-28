package com.chh.trustfort.accounting.controller.investment;

import com.chh.trustfort.accounting.dto.InvestmentCalculationResultDTO;
import com.chh.trustfort.accounting.service.investment.InvestmentCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/investments")
public class InvestmentCalculationController {

    @Autowired
    private InvestmentCalculationService calculationService;

    @GetMapping("/calculate")
    public InvestmentCalculationResultDTO calculate(
            @RequestParam Long assetClassId,
            @RequestParam BigDecimal amount,
            @RequestParam BigDecimal tenorYears
    ) {
        return calculationService.calculate(assetClassId, amount, tenorYears);
    }
}
