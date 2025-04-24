package com.chh.trustfort.accounting.enums;

public enum TransactionType {
    DEBIT,
    CREDIT;

    public boolean isCredit() {
        return this == CREDIT;
    }

    public boolean isDebit() {
        return this == DEBIT;
    }
}
