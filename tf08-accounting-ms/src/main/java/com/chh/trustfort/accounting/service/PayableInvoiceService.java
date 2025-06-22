package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.PayableInvoiceRequestDTO;
import com.chh.trustfort.accounting.model.PayableInvoice;

public interface PayableInvoiceService {

    /**
     * Submits a new payable invoice to the system.
     *
     * @param request the invoice submission payload
     * @return the saved PayableInvoice
     */
    PayableInvoice submitInvoice(PayableInvoiceRequestDTO request);
}