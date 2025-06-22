package com.chh.trustfort.accounting.enums;

/**
 *
 * @author DOfoleta
 */
public enum WalletStatus {
    ACTIVE,     // Wallet is fully operational and can process transactions
    SUSPENDED,  // Wallet is temporarily disabled, no transactions allowed
    LOCKED,
    CLOSED      // Wallet is permanently closed and cannot be reactivated
}
