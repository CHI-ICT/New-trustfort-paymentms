package com.chh.trustfort.payment.enums;

public enum ReverifyStatus {
    SUCCESS,             // Credited now
    ALREADY_VERIFIED,    // Already credited earlier (duplicate txRef)
    VERIFICATION_FAILED, // Flutterwave mismatch
    CREDIT_FAILED,       // Wallet credit failed
    ERROR                // Exception occurred
}
