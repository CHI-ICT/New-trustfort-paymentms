package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.enums.PayoutCategory;
import com.chh.trustfort.accounting.model.PayableInvoice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PayoutClassifierService {

    public PayoutCategory classify(PayableInvoice invoice) {
        if (invoice.getDescription() != null) {
            String desc = invoice.getDescription().toLowerCase();

            if (desc.contains("commission")) return PayoutCategory.COMMISSION;
            if (desc.contains("facultative")) return PayoutCategory.FACULTATIVE_OUT;
            if (desc.contains("treaty")) return PayoutCategory.TREATY;
            if (desc.contains("tax") || desc.contains("vat")) return PayoutCategory.TAX;
            if (desc.contains("vendor") || desc.contains("supplier")) return PayoutCategory.VENDOR_PAYMENT;
        }

        return PayoutCategory.OTHER;
    }
}
