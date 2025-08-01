package com.chh.trustfort.payment.service.ServiceImpl;

import com.chh.trustfort.payment.dto.PurchaseIntentDTO;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.model.PaymentReference;
import com.chh.trustfort.payment.payload.FundWalletRequestPayload;

public interface PaystackPaymentService {

    String initiatePaystackPayment(FundWalletRequestPayload request, AppUser appUser);
    boolean verifyPaystackPayment(String txRef);
    boolean reverifyAndCredit(PaymentReference reference);
    void reconcilePendingPaystackPayments();
    public String initiatePaystackPaymentForProduct(PurchaseIntentDTO dto, String txRef, AppUser appUser);
    boolean verifyPaystackProductPayment(String txRef);
}
