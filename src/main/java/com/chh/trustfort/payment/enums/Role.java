package com.chh.trustfort.payment.enums;

public enum Role {
    LOGIN("LOGIN"),
    ADMIN("ADMIN"),

    // Approval Rule Roles
    CREATE_APPROVAL_RULE("C_RUL"),
    UPDATE_APPROVER("U_APPROVER"),
    DELETE_APPROVAL_RULE("D_APPROVAL"),
    VIEW_APPROVAL_RULE("V_APPROVAL"),

    // Credit Line Roles
    CREATE_CREDIT_LINE("C_CREDIT"),
    VIEW_CREDIT_LINE("V_CREDIT"),
    UPDATE_CREDIT_LINE("U_CREDIT"),
    APPROVE_CREDIT_LINE("APPROVE_CREDIT_LINE");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    public String getValue() {
        return this.role;
    }
}
