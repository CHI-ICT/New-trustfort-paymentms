package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.dto.PurchaseIntentDTO;
import com.chh.trustfort.payment.model.PurchaseIntent;

public interface PurchaseIntentService {
    PurchaseIntent savePurchaseIntent(PurchaseIntentDTO dto, String txRef);
}
