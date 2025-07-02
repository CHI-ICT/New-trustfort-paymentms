package com.chh.trustfort.accounting.service.serviceImpl;

import com.chh.trustfort.accounting.enums.ReceiptStatus;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.Receipt;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.repository.ReceiptRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.ReceiptAlertService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReceiptAlertServiceImpl implements ReceiptAlertService {

    private final ReceiptRepository receiptRepository;
    private final AesService aesService;
    private final Gson gson;

    @Override
    public String getPendingReceipts(AppUser appUser) {
        OmniResponsePayload response = new OmniResponsePayload();

        try {
            LocalDate today = LocalDate.now();
            log.info("📢 Fetching pending receipts due before or on {}", today);

            List<Receipt> pendingReceipts = receiptRepository.findByStatusAndDueDateBefore(
                    ReceiptStatus.PENDING, today.plusDays(1)
            );

            response.setResponseCode("00");
            response.setResponseMessage("Pending receipts retrieved successfully");
            response.setData(pendingReceipts);
        } catch (Exception e) {
            log.error("❌ Failed to fetch pending receipts: {}", e.getMessage(), e);
            response.setResponseCode("06");
            response.setResponseMessage("Error retrieving pending receipts");
        }

        return aesService.encrypt(gson.toJson(response), appUser);
    }
}