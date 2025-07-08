package com.chh.trustfort.payment.service.ServiceImpl;

import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.model.PaymentReference;
import com.chh.trustfort.payment.payload.FundWalletRequestPayload;

public interface FlutterwavePaymentService {
    String initiateFlutterwavePayment(FundWalletRequestPayload request, AppUser appUser);
    boolean verifyFlutterwavePayment(String txRef, String transactionId);
    boolean reverifyAndCredit(PaymentReference reference);
    void reconcilePendingReferences();

}
