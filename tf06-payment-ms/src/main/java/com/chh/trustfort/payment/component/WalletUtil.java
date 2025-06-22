package com.chh.trustfort.payment.component;

import org.springframework.stereotype.Component;

/**
 *
 * @author DOfoleta
 */
@Component
public class WalletUtil {

    public boolean validateWalletId(String walletId) {
        if (walletId == null || walletId.length() != 10 || !walletId.matches("\\d{10}")) {
            return false;
        }

        String serial = walletId.substring(0, 9);
        int originalCheckDigit = Character.getNumericValue(walletId.charAt(9));
        int recalculatedCheckDigit = calculateCheckDigit(serial);

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

    public String generateAccountNumber() {
        // Step 1: Generate 9-digit random number (serial)
        String serial = String.format("%09d", (int) (Math.random() * 1_000_000_000));

        // Step 2: Calculate the check digit
        int checkDigit = calculateCheckDigit(serial);

        // Step 3: Combine serial and check digit
        return serial + checkDigit;
    }
}

