package com.chh.trustfort.accounting.service.investment.investmentImpl;

import com.chh.trustfort.accounting.dto.InvestmentCalculationResultDTO;
import com.chh.trustfort.accounting.model.AssetClass;
import com.chh.trustfort.accounting.repository.AssetClassRepository;
import com.chh.trustfort.accounting.service.investment.InvestmentCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class InvestmentCalculationServiceImpl implements InvestmentCalculationService {

    @Autowired
    private AssetClassRepository assetClassRepository;

    @Override
    public InvestmentCalculationResultDTO calculate(Long assetClassId, BigDecimal amount, BigDecimal tenorYears) {
        AssetClass asset = assetClassRepository.findById(assetClassId)
                .orElseThrow(() -> new IllegalArgumentException("Asset class not found"));

        BigDecimal baseInterestRate = asset.getBaseInterestRate(); // e.g., 0.10 = 10%
        BigDecimal dividendRate = asset.getDividendRate();         // e.g., 0.02 = 2%
        BigDecimal roiMultiplier = asset.getRoiMultiplier();       // e.g., 1.5

        // ROI = amount * baseRate * roiMultiplier * tenorYears
        BigDecimal roi = amount.multiply(baseInterestRate)
                .multiply(roiMultiplier)
                .multiply(tenorYears)
                .setScale(2, RoundingMode.HALF_UP);

        // Interest = amount * baseRate * tenorYears
        BigDecimal interest = amount.multiply(baseInterestRate)
                .multiply(tenorYears)
                .setScale(2, RoundingMode.HALF_UP);

        // Dividends = amount * dividendRate * tenorYears
        BigDecimal dividends = amount.multiply(dividendRate)
                .multiply(tenorYears)
                .setScale(2, RoundingMode.HALF_UP);

        InvestmentCalculationResultDTO result = new InvestmentCalculationResultDTO();
        result.setRoi(roi);
        result.setInterest(interest);
        result.setDividends(dividends);

        return result;
    }

}
