package com.chh.trustfort.accounting.service;// CreditNoteServiceImpl.java

import com.chh.trustfort.accounting.dto.CreditNoteRequestDTO;
import com.chh.trustfort.accounting.enums.CreditNoteStatus;
import com.chh.trustfort.accounting.model.CreditNote;
import com.chh.trustfort.accounting.model.DebitNote;
import com.chh.trustfort.accounting.repository.CreditNoteRepository;
import com.chh.trustfort.accounting.repository.DebitNoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditNoteServiceImpl implements CreditNoteService {

    private final CreditNoteRepository creditNoteRepository;
    private final DebitNoteRepository debitNoteRepository;

    @Override
    public CreditNote createCreditNote(CreditNoteRequestDTO request) {
        // Step 1: Find the debit note to link to
        DebitNote linkedDebitNote = debitNoteRepository.findById(request.getLinkedDebitNoteId())
                .orElseThrow(() -> new RuntimeException("Debit Note not found for ID: " + request.getLinkedDebitNoteId()));

        // Step 2: Build the credit note
        CreditNote note = CreditNote.builder()
                .reference(request.getReference() != null ? request.getReference() : "CRN-" + UUID.randomUUID())
                .payerEmail(linkedDebitNote.getPayerEmail())
                .customerName(linkedDebitNote.getCustomerName())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .issueDate(LocalDate.now())
                .remarks(request.getDescription()) // You map `description` to `remarks`
                .status(CreditNoteStatus.LINKED_TO_REVERSAL)
                .linkedDebitNote(linkedDebitNote)
                .createdBy(request.getCreatedBy())
                .createdAt(LocalDateTime.now())
                .build();

        // Step 3: Save and return
        return creditNoteRepository.save(note);
    }
}
