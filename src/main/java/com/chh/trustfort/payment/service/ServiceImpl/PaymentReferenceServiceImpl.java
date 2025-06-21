//package com.chh.trustfort.payment.service.ServiceImpl;
//
//
//import com.chh.trustfort.payment.Quintuple;
//import com.chh.trustfort.payment.Responses.SuccessResponse;
//import com.chh.trustfort.payment.component.RequestManager;
//import com.chh.trustfort.payment.enums.ReferenceStatus;
//import com.chh.trustfort.payment.model.PaymentReference;
//import com.chh.trustfort.payment.model.Users;
//import com.chh.trustfort.payment.payload.PaymentReferenceRequestPayload;
//import com.chh.trustfort.payment.payload.PaymentReferenceResponsePayload;
//import com.chh.trustfort.payment.repository.PaymentReferenceRepository;
//import com.chh.trustfort.payment.security.AesService;
//import com.chh.trustfort.payment.service.PaymentReferenceService;
//import com.google.gson.Gson;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import org.springframework.web.server.ResponseStatusException;
//
//import javax.persistence.EntityNotFoundException;
//import javax.servlet.http.HttpServletRequest;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.UUID;
//@Service
//@RequiredArgsConstructor
//public class PaymentReferenceServiceImpl implements PaymentReferenceService {
//
//    private final PaymentReferenceRepository paymentReferenceRepository;
//    private final AesService aesService;
//    private final Gson gson;
//
//    @Override
//    public PaymentReference generateReference(Users user, BigDecimal amount) {
//        String uniqueRef = "REF-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
//        PaymentReference reference = new PaymentReference();
//        reference.setReferenceCode(uniqueRef);
//        reference.setUser(user);
//        reference.setAmount(amount);
//        reference.setStatus(ReferenceStatus.PENDING);
//        reference.setCreatedAt(LocalDateTime.now());
//        return paymentReferenceRepository.save(reference);
//    }
//
//    @Override
//    public String generatePaymentReference(PaymentReferenceRequestPayload payload, Users user) {
//        PaymentReference reference = generateReference(user, payload.getAmount());
//        SuccessResponse response = new SuccessResponse();
//        response.setResponseCode("00");
//        response.setResponseMessage("Payment reference generated successfully");
//        response.setReferenceCode(reference.getReferenceCode());
//        return aesService.encrypt(gson.toJson(response), user.getEcred());
//    }
//
//    @Override
//    public PaymentReference getReferenceByCode(String referenceCode) {
//        return paymentReferenceRepository.findByReferenceCode(referenceCode)
//                .orElseThrow(() -> new RuntimeException("Reference not found"));
//    }
//
//    @Override
//    public PaymentReference generateReference(HttpServletRequest request, BigDecimal amount, String role, String rawRequestBody) {
//        throw new UnsupportedOperationException("This method is unused in current implementation");
//    }
//}
