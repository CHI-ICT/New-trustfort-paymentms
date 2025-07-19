package com.chh.trustfort.accounting.enums;

public enum AccountClassification {
    ASSET("1000"),
    LIABILITY("2000"),
    EQUITY("3000"),
    REVENUE("4000"),
    INCOME("4000"),
    EXPENSE("5000"),
    OTHER("9000");

    private final String code;

    AccountClassification(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
