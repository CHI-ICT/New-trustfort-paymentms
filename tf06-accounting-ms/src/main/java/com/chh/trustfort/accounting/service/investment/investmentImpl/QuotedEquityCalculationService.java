package com.chh.trustfort.accounting.service.investment.investmentImpl;

import com.chh.trustfort.accounting.model.QuotedEquityInvestment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class QuotedEquityCalculationService {

    private final MarketPriceService marketPriceApiClient;

    @Autowired
    public QuotedEquityCalculationService(MarketPriceService marketPriceApiClient) {
        this.marketPriceApiClient = marketPriceApiClient;
    }

    public void calculate(QuotedEquityInvestment investment) {
        if (investment.getNameOfStock() != null) {
            BigDecimal livePrice = marketPriceApiClient.fetchCurrentMarketPrice(investment.getNameOfStock());
            investment.setMarketPrice(livePrice);
        }

        if (investment.getQuantity() != null && investment.getMarketPrice() != null) {
            BigDecimal marketValue = investment.getMarketPrice().multiply(BigDecimal.valueOf(investment.getQuantity()));
            investment.setMarketValue(marketValue);
        }

        if (investment.getDisposal() != null
                && investment.getUnitPrice() != null
                && investment.getMarketPrice() != null
                && investment.getQuantity() != null) {

            BigDecimal gainLoss = investment.getMarketPrice()
                    .subtract(investment.getUnitPrice())
                    .multiply(BigDecimal.valueOf(investment.getQuantity()));

            investment.setGainOrLoss(gainLoss);
        }
    }
}

