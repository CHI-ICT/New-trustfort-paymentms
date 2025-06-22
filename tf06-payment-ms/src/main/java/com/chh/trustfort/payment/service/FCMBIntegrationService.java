package com.chh.trustfort.payment.service;

public interface FCMBIntegrationService {
    /**
     * Simulates the external FCMB API transfer.
     * @param settlementAccountNumber The settlement account number.
     * @param amount The amount to transfer.
     * @return true if the transfer is successful; false otherwise.
     */
    boolean transferFunds(String settlementAccountNumber, java.math.BigDecimal amount);
}
