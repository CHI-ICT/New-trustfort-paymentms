package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.CreateReceivableRequest;
import com.chh.trustfort.accounting.enums.ReceivableStatus;
import com.chh.trustfort.accounting.exception.ApiException;
import com.chh.trustfort.accounting.exception.ApiExceptions;
import com.chh.trustfort.accounting.model.Receivable;
import com.chh.trustfort.accounting.repository.ReceivableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReceivableServiceImpl implements ReceivableService {

    private final ReceivableRepository receivableRepository;

    @Override
    public Receivable createReceivable(CreateReceivableRequest request) {

        // Check for duplicates by email + currency (or by account number)
        boolean exists = receivableRepository.existsByCustomerEmailAndCurrency(
                request.getCustomerEmail(), request.getCurrency()
        );

        if (exists) {
            throw new ApiException("Receivable already exists for this customer and currency.",HttpStatus.CONFLICT);

        }

        Receivable receivable = new Receivable();
        receivable.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        receivable.setReference("REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        receivable.setCustomerName(request.getCustomerName());
        receivable.setCustomerEmail(request.getCustomerEmail());
        receivable.setCustomerAccount(request.getCustomerAccount());
        receivable.setCurrency(request.getCurrency());
        receivable.setAmount(request.getAmount());
        receivable.setBalance(request.getAmount());
        receivable.setStatus(ReceivableStatus.PENDING);
        receivable.setDueDate(request.getDueDate());
        receivable.setCreatedAt(LocalDateTime.now());
        receivable.setCreatedBy("system");

        return receivableRepository.save(receivable);
    }

    @Override
    public List<Receivable> getAllReceivables() {
        return receivableRepository.findAll();
    }
}
