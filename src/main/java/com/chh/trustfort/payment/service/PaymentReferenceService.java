package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.model.PaymentReference;
import com.chh.trustfort.payment.payload.PaymentReferenceRequestPayload;

import java.util.Optional;

public interface PaymentReferenceService {
    String generatePaymentReference(PaymentReferenceRequestPayload payload, AppUser user);
    Optional<PaymentReference> getReferenceByCode(String referenceCode);
}

