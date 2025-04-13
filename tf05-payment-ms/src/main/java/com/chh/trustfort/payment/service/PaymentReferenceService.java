package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.model.Users;
import com.chh.trustfort.payment.model.PaymentReference;
import com.chh.trustfort.payment.payload.PaymentReferenceRequestPayload;

import java.math.BigDecimal;

public interface PaymentReferenceService {
    PaymentReference generateReference(Users user, BigDecimal amount);
    PaymentReference getReferenceByCode(String referenceCode);
    String generatePaymentReference(PaymentReferenceRequestPayload payload, Users user);

}
