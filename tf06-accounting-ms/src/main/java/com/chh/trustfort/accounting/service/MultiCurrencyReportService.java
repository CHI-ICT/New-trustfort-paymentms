package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.MultiCurrencyReportRow;
import java.util.List;

public interface MultiCurrencyReportService {
    List<MultiCurrencyReportRow> getAllConvertedReceipts(String baseCurrency);
}