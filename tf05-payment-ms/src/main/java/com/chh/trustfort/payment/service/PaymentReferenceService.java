package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.model.Users;
import com.chh.trustfort.payment.model.PaymentReference;
import com.chh.trustfort.payment.payload.PaymentReferenceRequestPayload;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

public interface PaymentReferenceService {

    //    @Override
    //    public PaymentReference generateReference(Users user, BigDecimal amount) {
    //        String uniqueRef = "REF-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    //        PaymentReference reference = new PaymentReference();
    //        reference.setReferenceCode(uniqueRef);
    //        reference.setUser(user);
    //        reference.setAmount(amount);
    //        reference.setStatus(ReferenceStatus.PENDING);
    //        reference.setCreatedAt(LocalDateTime.now());
    //        return paymentReferenceRepository.save(reference);
    //    }
    PaymentReference generateReference(Users user, BigDecimal amount);

    PaymentReference generateReference(HttpServletRequest request, BigDecimal amount, String role, String rawRequestBody);

    PaymentReference getReferenceByCode(String referenceCode);

    String generatePaymentReference(PaymentReferenceRequestPayload payload, Users user);

}
