package com.chh.trustfort.payment.component;

import org.springframework.stereotype.Component;

/**
 *
 * @author DOfoleta
 */
@Component
public class WalletUtil {

    // ✅ Generates a 10-digit account number (9-digit serial + 1 check digit)
    public String generateAccountNumber() {
        String serial = String.format("%09d", (int) (Math.random() * 1_000_000_000));
        int checkDigit = calculateCheckDigit(serial);
        return serial + checkDigit;
    }

    // ✅ Validates a generated account number
    public boolean validateAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() != 10 || !accountNumber.matches("\\d{10}")) {
            return false;
        }

        String serial = accountNumber.substring(0, 9);
        int originalCheckDigit = Character.getNumericValue(accountNumber.charAt(9));
        int recalculatedCheckDigit = calculateCheckDigit(serial);

        return originalCheckDigit == recalculatedCheckDigit;
    }

    // ✅ Luhn-like check digit algorithm
    public int calculateCheckDigit(String number) {
        int sum = 0;
        boolean alternate = false;

        for (int i = number.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(number.charAt(i));
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }
            sum += digit;
            alternate = !alternate;
        }

        return (10 - (sum % 10)) % 10;
    }
}
