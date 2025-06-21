package com.chh.trustfort.payment.Util;

public class PhoneNumberUtil {

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.matches("^\\+?[0-9]{10,15}$");
    }
}
