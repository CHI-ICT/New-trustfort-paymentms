package com.chh.trustfort.accounting.service.serviceImpl;// CreditNoteServiceImpl.java

import com.chh.trustfort.accounting.dto.CreditNoteRequestDTO;
import com.chh.trustfort.accounting.enums.CreditNoteStatus;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.CreditNote;
import com.chh.trustfort.accounting.model.DebitNote;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.repository.CreditNoteRepository;
import com.chh.trustfort.accounting.repository.DebitNoteRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.CreditNoteService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditNoteServiceImpl implements CreditNoteService {

    private final CreditNoteRepository creditNoteRepository;
    private final DebitNoteRepository debitNoteRepository;
    private final AesService aesService;
    private final MessageSource messageSource;
    private final Gson gson;

    @Override
    public String createCreditNote(CreditNoteRequestDTO request, AppUser appUser) {
        OmniResponsePayload response = new OmniResponsePayload();
        response.setResponseCode("06");
        response.setResponseMessage(messageSource.getMessage("failed", null, Locale.ENGLISH));

        try {
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
                    .remarks(request.getDescription())
                    .status(CreditNoteStatus.LINKED_TO_REVERSAL)
                    .linkedDebitNote(linkedDebitNote)
                    .createdBy(request.getCreatedBy())
                    .createdAt(LocalDateTime.now())
                    .build();

            CreditNote saved = creditNoteRepository.save(note);
            log.info("✅ Credit Note created with reference: {}", saved.getReference());

            response.setResponseCode("00");
            response.setResponseMessage("Credit note created successfully");
//            response.setData(saved);
        } catch (Exception e) {
            log.error("❌ Failed to create credit note: {}", e.getMessage(), e);
            response.setResponseMessage("Failed to create credit note: " + e.getMessage());
        }

        return aesService.encrypt(gson.toJson(response), appUser);
    }
}
