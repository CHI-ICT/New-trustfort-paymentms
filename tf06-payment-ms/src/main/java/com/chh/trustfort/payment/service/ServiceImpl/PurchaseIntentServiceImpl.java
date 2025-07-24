package com.chh.trustfort.payment.service.ServiceImpl;

import com.chh.trustfort.payment.dto.PurchaseIntentDTO;
import com.chh.trustfort.payment.model.PurchaseIntent;
import com.chh.trustfort.payment.repository.PurchaseIntentRepository;
import com.chh.trustfort.payment.service.PurchaseIntentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PurchaseIntentServiceImpl implements PurchaseIntentService {

    private final PurchaseIntentRepository purchaseIntentRepository;

    @Override
    public PurchaseIntent savePurchaseIntent(PurchaseIntentDTO dto, String txRef) {
        PurchaseIntent intent = PurchaseIntent.builder()
                .userId(dto.getUserId())
                .amount(dto.getAmount())
                .stringifiedData(dto.getStringifiedData())
                .status("PENDING")
                .txRef(txRef)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return purchaseIntentRepository.save(intent);
    }
}
