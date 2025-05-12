package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.enums.ReceiptSource;
import com.chh.trustfort.accounting.enums.ReceiptStatus;
import com.chh.trustfort.accounting.model.Receipt;
import com.chh.trustfort.accounting.payload.ReceiptGenerationRequest;
import com.chh.trustfort.accounting.repository.ReceiptRepository;
import com.chh.trustfort.accounting.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReceiptServiceImpl implements ReceiptService {

    private final ReceiptRepository receiptRepository;

    @Override
    public Receipt generateReceipt(ReceiptGenerationRequest request) {
        Receipt receipt = new Receipt();
        receipt.setReceiptNumber("RCT-" + UUID.randomUUID());
        receipt.setPayerName(request.getPayerName());
        receipt.setPayerEmail(request.getPayerEmail());
        receipt.setAmount(request.getAmount());
        receipt.setCurrency(request.getCurrency());
        receipt.setPaymentReference(request.getPaymentReference());
        receipt.setReceiptDate(LocalDateTime.now());
        receipt.setSource(request.getSource());
        receipt.setStatus(ReceiptStatus.CONFIRMED);
        receipt.setCreatedAt(LocalDateTime.now());
        receipt.setCreatedBy(request.getCreatedBy());
        return receiptRepository.save(receipt);
    }
}
