package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.enums.PayoutCategory;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.PayableInvoice;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.security.AesService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PayoutClassifierService {

    private  AesService aesService;
    private  Gson gson;

    public PayoutCategory resolveCategory(PayableInvoice invoice) {
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

    public String classifyInvoice(PayableInvoice invoice, AppUser appUser) {
        try {
            PayoutCategory category = PayoutCategory.OTHER;

            if (invoice.getDescription() != null) {
                String desc = invoice.getDescription().toLowerCase();

                if (desc.contains("commission")) category = PayoutCategory.COMMISSION;
                else if (desc.contains("facultative")) category = PayoutCategory.FACULTATIVE_OUT;
                else if (desc.contains("treaty")) category = PayoutCategory.TREATY;
                else if (desc.contains("tax") || desc.contains("vat")) category = PayoutCategory.TAX;
                else if (desc.contains("vendor") || desc.contains("supplier")) category = PayoutCategory.VENDOR_PAYMENT;
            }

            invoice.setPayoutCategory(category);
            return aesService.encrypt(gson.toJson(invoice), appUser);

        } catch (Exception e) {
            log.error("❌ Failed to classify invoice: {}", e.getMessage());
            OmniResponsePayload error = new OmniResponsePayload();
            error.setResponseCode("99");
            error.setResponseMessage("Classification failed: " + e.getMessage());
            return aesService.encrypt(gson.toJson(error), appUser);
        }

    }
}