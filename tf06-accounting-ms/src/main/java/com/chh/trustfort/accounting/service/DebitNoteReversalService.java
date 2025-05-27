package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.model.CreditNote;
import com.chh.trustfort.accounting.model.DebitNote;
import com.chh.trustfort.accounting.enums.CreditNoteStatus;
import com.chh.trustfort.accounting.enums.DebitNoteStatus;
import com.chh.trustfort.accounting.repository.CreditNoteRepository;
import com.chh.trustfort.accounting.repository.DebitNoteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DebitNoteReversalService {

    private final DebitNoteRepository debitNoteRepository;
    private final CreditNoteRepository creditNoteRepository;

    public void reverseDebitNoteAndCreateCredit(Long debitNoteId, String reason, String createdBy) {
        // Step 1: Fetch the debit note by ID
        DebitNote debitNote = debitNoteRepository.findById(debitNoteId)
                .orElseThrow(() -> new RuntimeException("DebitNote not found with ID: " + debitNoteId));

        // Step 2: Mark the debit note as reversed
        debitNote.setStatus(DebitNoteStatus.REVERSED);
        debitNote.setReversalReason(reason);
        debitNote.setReversedAt(LocalDateTime.now());
        debitNoteRepository.save(debitNote);

        // Step 3: Create and save a new credit note to reverse the debit note
        CreditNote creditNote = new CreditNote();
        creditNote.setReference("CRN-" + UUID.randomUUID());
        creditNote.setPayerEmail(debitNote.getPayerEmail());
        creditNote.setCustomerName(debitNote.getCustomerName());
        creditNote.setAmount(debitNote.getAmount());
        creditNote.setCurrency(debitNote.getCurrency());
        creditNote.setIssueDate(LocalDate.now());
        creditNote.setStatus(CreditNoteStatus.LINKED_TO_REVERSAL);
        creditNote.setLinkedDebitNote(debitNote);
        creditNote.setCreatedAt(LocalDateTime.now());
        creditNote.setCreatedBy(createdBy);

        creditNoteRepository.save(creditNote);

        log.info("Debit note {} reversed. Credit note {} created.", debitNoteId, creditNote.getReference());
    }
}
