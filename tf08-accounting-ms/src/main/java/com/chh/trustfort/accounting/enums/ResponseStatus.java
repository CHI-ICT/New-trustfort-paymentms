package com.chh.trustfort.accounting.enums;

public enum ResponseStatus {
    SUCCESS("SUCCESS"),
    ERROR("ERROR");

    private final String value;

    ResponseStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
