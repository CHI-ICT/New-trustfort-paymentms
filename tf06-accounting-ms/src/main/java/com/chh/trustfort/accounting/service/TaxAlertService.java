package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.component.EmailNotificationClient;
import com.chh.trustfort.accounting.enums.TaxType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaxAlertService {

    private static final int DAYS_BEFORE_ALERT = 5; // Alert 5 days before deadline

    private final EmailNotificationClient emailNotificationClient;

    public void checkUpcomingFilingDeadlines() {
        LocalDate today = LocalDate.now();

        for (TaxType taxType : TaxType.values()) {
            LocalDate nextFilingDate = calculateNextFilingDeadline(taxType, today);

            if (nextFilingDate != null) {
                long daysUntilDeadline = java.time.temporal.ChronoUnit.DAYS.between(today, nextFilingDate);

                if (daysUntilDeadline >= 0 && daysUntilDeadline <= DAYS_BEFORE_ALERT) {
                    log.info("🔔 Filing deadline approaching for {}: due on {}", taxType, nextFilingDate);

                    // Send email alert
                    sendTaxFilingReminder(
                            "finance@example.com",  // ✅ Replace with dynamic email or config later
                            taxType.name(),
                            nextFilingDate
                    );
                }
            }
        }
    }

    private void sendTaxFilingReminder(String email, String taxType, LocalDate dueDate) {
        String subject = "📌 Tax Filing Reminder: " + taxType;
        String body = "<p>Dear Finance Team,</p>" +
                "<p>This is a reminder that <strong>" + taxType + "</strong> filing is due by <strong>" + dueDate + "</strong>.</p>" +
                "<p>Please ensure submission is done before the deadline.</p>" +
                "<br><p>Regards,<br>Trustfort TaxBot</p>";

        emailNotificationClient.sendEmail(email, subject, body, false);
    }

    private LocalDate calculateNextFilingDeadline(TaxType taxType, LocalDate today) {
        // Basic logic: assume all filings due on the 20th of next month
        return today.withDayOfMonth(1).plusMonths(1).withDayOfMonth(20);
    }
}
