// PaymentMovementService.java
package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.PaymentMovementRequest;

public interface PaymentMovementService {
    void movePayment(PaymentMovementRequest request);
}