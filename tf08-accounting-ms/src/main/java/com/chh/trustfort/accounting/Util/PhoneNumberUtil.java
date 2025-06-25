package com.chh.trustfort.accounting.Util;

public class PhoneNumberUtil {

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("^\\+?[0-9]{10,15}$");
    }
}
