package com.chh.trustfort.payment.enums;

public enum CommissionType {
    TRANSACTION,   // General transaction-based commission
    REFERRAL,      // Commission earned from referrals
    FIXED,         // Fixed commission amount for specific services
    WITHDRAWAL_FEE,// Fee when withdrawing funds
    TRANSFER_FEE,  // Fee for sending money to another wallet
    DEPOSIT_FEE    // Fee for funding a wallet
}
