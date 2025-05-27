package com.chh.trustfort.accounting.service.investment.investmentImpl;

import com.chh.trustfort.accounting.enums.InvestmentType;
import com.chh.trustfort.accounting.model.Investment;
import com.chh.trustfort.accounting.service.investment.AccountingMapperService;
import org.springframework.stereotype.Service;

@Service
public class AccountingMapperServiceImpl implements AccountingMapperService {
    @Override
    public String determineSide(Investment investment) {
        InvestmentType type = investment.getType();

        switch (type) {
            case FIXED_DEPOSIT:
            case TREASURY_BILL:
                return "DEBIT"; // money is going out to invest
            case MUTUAL_FUND:
            case BOND:
            case STOCK:
                return "CREDIT"; // assuming return recognition or selloff proceeds
            default:
                return "UNKNOWN";
        }
    }
}

