package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.enums.ReceiptStatus;
import com.chh.trustfort.accounting.model.Receipt;
import com.chh.trustfort.accounting.repository.ReceiptRepository;
import com.chh.trustfort.accounting.service.ReceiptAlertService;
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

    @Override
    public List<Receipt> getPendingReceipts() {
        LocalDate today = LocalDate.now();
        log.info("Fetching pending receipts due before or on {}", today);
        return receiptRepository.findByStatusAndDueDateBefore(ReceiptStatus.PENDING, today.plusDays(1));
    }
}