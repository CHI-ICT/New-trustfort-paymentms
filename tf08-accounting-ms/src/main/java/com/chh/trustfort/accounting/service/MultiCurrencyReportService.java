package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.MultiCurrencyReportRow;
import com.chh.trustfort.accounting.model.AppUser;

import java.util.List;

public interface MultiCurrencyReportService {
//    List<MultiCurrencyReportRow> getAllConvertedReceipts(String baseCurrency);
    String getAllConvertedReceipts(String baseCurrency, AppUser user);
}