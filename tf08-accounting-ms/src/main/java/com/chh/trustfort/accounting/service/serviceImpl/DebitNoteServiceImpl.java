package com.chh.trustfort.accounting.service.serviceImpl;// DebitNoteServiceImpl.java

import com.chh.trustfort.accounting.component.ResponseCode;
import com.chh.trustfort.accounting.dto.ReversalRequestDTO;
import com.chh.trustfort.accounting.enums.DebitNoteStatus;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.DebitNote;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.repository.DebitNoteRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.DebitNoteService;
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
public class DebitNoteServiceImpl implements DebitNoteService {

    private final DebitNoteRepository debitNoteRepository;
    private final AesService aesService;
    private final Gson gson;
    private final MessageSource messageSource;

    @Override
    public String reverseDebitNote(ReversalRequestDTO request, AppUser appUser) {
        OmniResponsePayload response = new OmniResponsePayload();
        response.setResponseCode(ResponseCode.FAILED_TRANSACTION.getResponseCode());
        response.setResponseMessage(messageSource.getMessage("failed", null, Locale.ENGLISH));

        try {
            DebitNote note = debitNoteRepository.findById(request.getDebitNoteId())
                    .orElseThrow(() -> new RuntimeException("Debit Note not found"));

            if (note.getStatus() == DebitNoteStatus.REVERSED) {
                response.setResponseMessage("Debit Note has already been reversed.");
                return aesService.encrypt(gson.toJson(response), appUser);
            }

            note.setStatus(DebitNoteStatus.REVERSED);
            note.setReversalReason(request.getReason());
            note.setReversedBy(request.getCreatedBy());
            note.setReversedAt(LocalDateTime.now());
            debitNoteRepository.save(note);

            response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
            response.setResponseMessage("Debit note reversed successfully.");
        } catch (Exception e) {
            log.error("❌ Failed to reverse debit note", e);
            response.setResponseMessage("Failed to reverse debit note: " + e.getMessage());
        }

        return aesService.encrypt(gson.toJson(response), appUser);
    }

    @Override
    public String linkNewDebitNoteToOld(Long oldNoteId, DebitNote newNotePayload, String createdBy, AppUser appUser) {
        OmniResponsePayload response = new OmniResponsePayload();
        response.setResponseCode(ResponseCode.FAILED_TRANSACTION.getResponseCode());
        response.setResponseMessage("Failed to create debit note link");

        try {
            DebitNote oldNote = debitNoteRepository.findById(oldNoteId)
                    .orElseThrow(() -> new RuntimeException("Old Debit Note not found"));

            DebitNote newNote = DebitNote.builder()
                    .reference("DN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .payerEmail(newNotePayload.getPayerEmail())
                    .customerName(newNotePayload.getCustomerName())
                    .amount(newNotePayload.getAmount())
                    .currency(newNotePayload.getCurrency())
                    .issueDate(LocalDate.now())
                    .dueDate(newNotePayload.getDueDate())
                    .status(DebitNoteStatus.PENDING)
                    .replaces(oldNote)
                    .createdAt(LocalDateTime.now())
                    .createdBy(createdBy)
                    .build();

            DebitNote saved = debitNoteRepository.save(newNote);
            response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
            response.setResponseMessage("New debit note linked successfully.");
//            response.setData(saved);
        } catch (Exception e) {
            log.error("❌ Error linking debit note", e);
            response.setResponseMessage("Error linking debit note: " + e.getMessage());
        }

        return aesService.encrypt(gson.toJson(response), appUser);
    }
}
