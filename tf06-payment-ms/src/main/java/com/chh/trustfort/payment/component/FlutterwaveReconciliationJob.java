package com.chh.trustfort.payment.component;

import com.chh.trustfort.payment.enums.ReferenceStatus;
import com.chh.trustfort.payment.model.PaymentReference;
import com.chh.trustfort.payment.repository.PaymentReferenceRepository;
import com.chh.trustfort.payment.service.ServiceImpl.FlutterwavePaymentService;
import com.chh.trustfort.payment.service.ServiceImpl.FlutterwavePaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@Lazy
@RequiredArgsConstructor
public class FlutterwaveReconciliationJob {

    private final PaymentReferenceRepository paymentReferenceRepository;
    private final FlutterwavePaymentService flutterwavePaymentService;

    /**
     * Reconciliation job runs every 5 minutes to verify pending references.
     */
    @Scheduled(cron = "0 */5 * * * *") // Every 5 minutes
    public void reconcilePendingFlutterwaveTransactions() {
        log.info("🔄 Starting Flutterwave reconciliation job...");

        List<PaymentReference> pendingRefs = paymentReferenceRepository.findByStatusAndGateway(
                ReferenceStatus.PENDING, "FLW"
        );

        if (pendingRefs.isEmpty()) {
            log.info("✅ No pending Flutterwave transactions to reconcile.");
            return;
        }

        for (PaymentReference reference : pendingRefs) {
            try {
                log.info("🔍 Attempting to verify tx_ref: {}", reference.getTxRef());
                flutterwavePaymentService.reverifyAndCredit(reference);
            } catch (Exception e) {
                log.error("❌ Error during reconciliation for tx_ref {}: {}", reference.getTxRef(), e.getMessage());
            }
        }

        log.info("✅ Flutterwave reconciliation job completed.");
    }
}
