package com.chh.trustfort.payment.constant;

/**
 *
 * @author dofoleta
 */
public enum OtpPurpose {
    BIOMETRIC_RESET("BR"),
    PASSWORD_RESET("PR"),
    PIN_RESET("PI"),
    ACCOUNT_OPENING("AO"),
    REGISTRATION("RE"),
    DEVICE_SWITCH("DS");

    private final String purposeCode;

    public String getPurposeCode() {
        return this.purposeCode;
    }

    OtpPurpose(String purposeCode) {
        this.purposeCode = purposeCode;
    }
}
