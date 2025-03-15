package com.chh.trustfort.payment.component;

import org.springframework.stereotype.Component;

/**
 *
 * @author DOfoleta
 */
@Component
public class WalletUtil {

    public boolean validateWalletId(String walletId) {
        // Ensure the wallet ID is 10 digits
        if (walletId == null || walletId.length() != 10 || !walletId.matches("\\d{10}")) {
            return false;
        }

        // Extract the serial number (first 9 digits) and check digit (last digit)
        String serial = walletId.substring(0, 9);
        int originalCheckDigit = Character.getNumericValue(walletId.charAt(9));

        // Recalculate the check digit using the Luhn algorithm
        int recalculatedCheckDigit = calculateCheckDigit(serial);

        // Compare the original and recalculated check digits
        return originalCheckDigit == recalculatedCheckDigit;
    }

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
