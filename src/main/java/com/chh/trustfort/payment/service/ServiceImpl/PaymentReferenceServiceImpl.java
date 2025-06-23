package com.chh.trustfort.payment.service.ServiceImpl;


import com.chh.trustfort.payment.Responses.SuccessResponse;
import com.chh.trustfort.payment.enums.ReferenceStatus;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.model.PaymentReference;
import com.chh.trustfort.payment.payload.PaymentReferenceRequestPayload;
import com.chh.trustfort.payment.repository.PaymentReferenceRepository;
import com.chh.trustfort.payment.security.AesService;
import com.chh.trustfort.payment.service.PaymentReferenceService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentReferenceServiceImpl implements PaymentReferenceService {

    private final PaymentReferenceRepository paymentReferenceRepository;
    private final AesService aesService;
    private final Gson gson;

    @Override
    public String generatePaymentReference(PaymentReferenceRequestPayload payload, AppUser user) {
        String uniqueRef = "REF-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

        PaymentReference reference = new PaymentReference();
        reference.setReferenceCode(uniqueRef);
//        reference.setAppUser(user); // assumes PaymentReference has a field `AppUser appUser`
        reference.setAmount(payload.getAmount());
        reference.setStatus(ReferenceStatus.PENDING);
        reference.setCreatedAt(LocalDateTime.now());

        paymentReferenceRepository.save(reference);

        SuccessResponse response = new SuccessResponse();
        response.setResponseCode("00");
        response.setResponseMessage("Payment reference generated successfully");
        response.setReferenceCode(reference.getReferenceCode());

//        return aesService.encrypt(gson.toJson(response), user.getEcred());
        return aesService.encrypt(gson.toJson(response), user);

    }

    @Override
    public Optional<PaymentReference> getReferenceByCode(String referenceCode) {
        return paymentReferenceRepository.findByReferenceCode(referenceCode);
    }
}