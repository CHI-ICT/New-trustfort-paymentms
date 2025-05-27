package com.chh.trustfort.accounting.service.investment;

import com.chh.trustfort.accounting.model.Investment;

public interface AccountingMapperService {
    String determineSide(Investment investment);
}
