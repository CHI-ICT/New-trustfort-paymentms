package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.CreateReceivableRequest;
import com.chh.trustfort.accounting.dto.ReceivableRequest;
import com.chh.trustfort.accounting.model.Receivable;

import java.util.List;

public interface ReceivableService {
    Receivable createReceivable(CreateReceivableRequest request);
    List<Receivable> getAllReceivables();
}
