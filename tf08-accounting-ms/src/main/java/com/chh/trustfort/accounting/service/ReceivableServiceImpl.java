package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.CreateReceivableRequest;
import com.chh.trustfort.accounting.enums.DebitNoteStatus;
import com.chh.trustfort.accounting.enums.ReceivableStatus;
import com.chh.trustfort.accounting.exception.ApiException;
import com.chh.trustfort.accounting.exception.ApiExceptions;
import com.chh.trustfort.accounting.model.DebitNote;
import com.chh.trustfort.accounting.model.Receivable;
import com.chh.trustfort.accounting.repository.DebitNoteRepository;
import com.chh.trustfort.accounting.repository.ReceivableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReceivableServiceImpl implements ReceivableService {

    private final ReceivableRepository receivableRepository;
    private final DebitNoteRepository debitNoteRepository;

    @Override
    public Receivable createReceivable(CreateReceivableRequest request) {

        boolean exists = receivableRepository.existsByPayerEmailAndCurrency(
                request.getPayerEmail(), request.getCurrency()
        );

        if (exists) {
            throw new ApiException("Receivable already exists for this customer and currency.", HttpStatus.CONFLICT);
        }

        // Step 1: Build and save the DebitNote first
        DebitNote debitNote = DebitNote.builder()
                .reference("DN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .payerEmail(request.getPayerEmail())
                .customerName(request.getCustomerName())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status(DebitNoteStatus.PENDING)
                .issueDate(LocalDate.now())
                .dueDate(request.getDueDate())
                .createdBy("system")
                .createdAt(LocalDateTime.now())
                .build();

        debitNote = debitNoteRepository.save(debitNote); // must save first!

        // Step 2: Create and link the receivable
        Receivable receivable = new Receivable();
        receivable.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        receivable.setReference("REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        receivable.setCustomerName(request.getCustomerName());
        receivable.setPayerEmail(request.getPayerEmail());
        receivable.setCustomerAccount(request.getCustomerAccount());
        receivable.setCurrency(request.getCurrency());
        receivable.setAmount(request.getAmount());
        receivable.setBalance(request.getAmount());
        receivable.setStatus(ReceivableStatus.PENDING);
        receivable.setDueDate(request.getDueDate());
        receivable.setCreatedAt(LocalDateTime.now());
        receivable.setCreatedBy("system");
        receivable.setDebitNote(debitNote); // link AFTER saving the debit note

        return receivableRepository.save(receivable);
    }


    @Override
    public List<Receivable> getAllReceivables() {
        return receivableRepository.findAll();
    }
}