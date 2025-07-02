// PaymentMovementService.java
package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.PaymentMovementRequest;
import com.chh.trustfort.accounting.model.AppUser;

public interface PaymentMovementService {
    String movePayment(PaymentMovementRequest request, AppUser appUser);
}