// PaymentMovementServiceImpl.java
package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.PaymentMovementRequest;
import com.chh.trustfort.accounting.enums.ReceivableStatus;
import com.chh.trustfort.accounting.exception.ApiException;
import com.chh.trustfort.accounting.model.PaymentMovement;
import com.chh.trustfort.accounting.model.Receivable;
import com.chh.trustfort.accounting.repository.PaymentMovementRepository;
import com.chh.trustfort.accounting.repository.ReceivableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentMovementServiceImpl implements PaymentMovementService {

    private final ReceivableRepository receivableRepository;
    private final PaymentMovementRepository paymentMovementRepository;

    @Override
    public void movePayment(PaymentMovementRequest request) {
//        Receivable fromReceivable = receivableRepository.findByReferenceIgnoreCase(
//                request.getFromReceivableReference().trim()
//        ).orElseThrow(() -> new ApiException("Source receivable not found",HttpStatus.CONFLICT));
//
//        Receivable toReceivable = receivableRepository.findByReferenceIgnoreCase(
//                request.getToReceivableReference().trim()
//        ).orElseThrow(() -> new ApiException("Target receivable not found",HttpStatus.CONFLICT));

        Receivable source = receivableRepository.findByReference(request.getSourceReceivableRef())
                .orElseThrow(() -> new ApiException("Source receivable not found",HttpStatus.CONFLICT));
        Receivable destination = receivableRepository.findByReference(request.getDestinationReceivableRef())
                .orElseThrow(() -> new ApiException("Destination receivable not found",HttpStatus.CONFLICT));

        if (source.getBalance().compareTo(request.getAmount()) < 0) {
            throw new ApiException("Insufficient balance in source receivable", HttpStatus.CONFLICT);
        }

        // Debit source
        source.setBalance(source.getBalance().subtract(request.getAmount()));
        if (source.getBalance().compareTo(BigDecimal.ZERO) == 0) {
            source.setStatus(ReceivableStatus.PAID);
        }

        // Credit destination
        destination.setBalance(destination.getBalance().add(request.getAmount()));
        if (destination.getBalance().compareTo(destination.getAmount()) >= 0) {
            destination.setStatus(ReceivableStatus.PAID);
        }

        receivableRepository.save(source);
        receivableRepository.save(destination);

        PaymentMovement movement = PaymentMovement.builder()
                .reference("PMT-MOV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .sourceReceivableRef(source.getReference())
                .destinationReceivableRef(destination.getReference())
                .amount(request.getAmount())
                .reason(request.getReason())
                .movedBy(request.getMovedBy())
                .movedAt(LocalDateTime.now())
                .build();

        paymentMovementRepository.save(movement);
    }
}