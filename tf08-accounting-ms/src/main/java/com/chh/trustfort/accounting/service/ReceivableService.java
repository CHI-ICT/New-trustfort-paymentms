package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.CreateReceivableRequest;
import com.chh.trustfort.accounting.dto.ReceivableRequest;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.Receivable;

import java.util.List;

public interface ReceivableService {
    String createReceivable(CreateReceivableRequest request, AppUser appUser);
    String getAllReceivables(AppUser appUser);
}
