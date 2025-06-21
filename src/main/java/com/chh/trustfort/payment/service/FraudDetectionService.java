package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.enums.TransactionType;
import com.chh.trustfort.payment.model.LedgerEntry;
import com.chh.trustfort.payment.model.Users;
import com.chh.trustfort.payment.repository.LedgerEntryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudDetectionService {

    private final LedgerEntryRepository ledgerEntryRepository;

    // For tracking failed OTPs in-memory (could use Redis or DB in production)
    private final ConcurrentHashMap<Long, Integer> otpFailureMap = new ConcurrentHashMap<>();

    private static final int MAX_WITHDRAWALS_PER_DAY = 5;
    private static final BigDecimal MAX_DAILY_WITHDRAWAL_AMOUNT = new BigDecimal("1000000");
    private static final int MAX_OTP_FAILURES = 3;

    public boolean isFraudulentWithdrawal(Users user, BigDecimal amount) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        List<LedgerEntry> todayWithdrawals = ledgerEntryRepository.findByWallet_UsersAndTransactionTypeAndCreatedAtAfter(
                user, TransactionType.DEBIT, startOfDay);

        BigDecimal totalToday = todayWithdrawals.stream()
                .map(LedgerEntry::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (todayWithdrawals.size() >= MAX_WITHDRAWALS_PER_DAY) {
            log.warn("User {} exceeded daily withdrawal count.", user.getUserName());
            return true;
        }

        if (totalToday.add(amount).compareTo(MAX_DAILY_WITHDRAWAL_AMOUNT) > 0) {
            log.warn("User {} exceeded daily withdrawal amount.", user.getUserName());
            return true;
        }

        return false;
    }

    public void recordFailedOtpAttempt(Long userId) {
        otpFailureMap.merge(userId, 1, Integer::sum);
    }

    public boolean isUserBlockedDueToOtpFailures(Long userId) {
        return otpFailureMap.getOrDefault(userId, 0) >= MAX_OTP_FAILURES;
    }

    public void resetOtpFailures(Long userId) {
        otpFailureMap.remove(userId);
    }
}

