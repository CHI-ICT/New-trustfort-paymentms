package com.chh.trustfort.payment.service.ServiceImpl;


import com.chh.trustfort.payment.enums.ReferenceStatus;
import com.chh.trustfort.payment.model.PaymentReference;
import com.chh.trustfort.payment.model.Users;
import com.chh.trustfort.payment.model.Wallet;
import com.chh.trustfort.payment.payload.PaymentReferenceRequestPayload;
import com.chh.trustfort.payment.payload.PaymentReferenceResponsePayload;
import com.chh.trustfort.payment.repository.PaymentReferenceRepository;
import com.chh.trustfort.payment.security.AesService;
import com.chh.trustfort.payment.service.PaymentReferenceService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class PaymentReferenceServiceImpl implements PaymentReferenceService {

    @Autowired
    private PaymentReferenceRepository paymentReferenceRepository;

    @Autowired
    private AesService aesService;

    @Autowired
    private Gson gson;


    @Override
    public PaymentReference generateReference(Users user, BigDecimal amount) {
        String uniqueRef = "REF-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        PaymentReference reference = new PaymentReference();
        reference.setReferenceCode(uniqueRef);
        reference.setUser(user);
        reference.setAmount(amount);
        reference.setStatus(ReferenceStatus.PENDING);
        reference.setCreatedAt(LocalDateTime.now());
        return paymentReferenceRepository.save(reference);
    }

    @Override
    public PaymentReference getReferenceByCode(String referenceCode) {
        return paymentReferenceRepository.findByReferenceCode(referenceCode)
                .orElseThrow(() -> new RuntimeException("Reference not found"));
    }

    @Override
    public String generatePaymentReference(PaymentReferenceRequestPayload payload, Users user) {
//        Wallet wallet = getWalletOrThrow(request.getWalletId());
//
//        if (!wallet.getUsers().getId().equals(user.getId())) {
//            return new GenerateReferenceResponse(null, "Unauthorized wallet access");
//        }
        BigDecimal amount = payload.getAmount();

        // Create and set payment reference fields using setters
        PaymentReference reference = new PaymentReference();
        reference.setReferenceCode("REF-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        reference.setUser(user);
        reference.setAmount(amount);
        reference.setStatus(ReferenceStatus.PENDING);
        reference.setCreatedAt(LocalDateTime.now());
        reference.setUsedAt(null); // Optional, but explicit

        // Save to the database
        paymentReferenceRepository.save(reference);

        // Prepare response
        PaymentReferenceResponsePayload response = new PaymentReferenceResponsePayload();
        response.setReferenceCode(reference.getReferenceCode());
        response.setAmount(reference.getAmount());
        response.setStatus(reference.getStatus().name());
        response.setMessage("Reference generated successfully");

        // Encrypt and return (or plain JSON for now)
        // return aesService.encrypt(gson.toJson(response), user.getEcred());
        return gson.toJson(response);
    }


}
