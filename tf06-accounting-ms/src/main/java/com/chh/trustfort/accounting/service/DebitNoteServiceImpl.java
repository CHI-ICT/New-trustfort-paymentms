package com.chh.trustfort.accounting.service;// DebitNoteServiceImpl.java

import com.chh.trustfort.accounting.dto.ReversalRequestDTO;
import com.chh.trustfort.accounting.enums.DebitNoteStatus;
import com.chh.trustfort.accounting.model.DebitNote;
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
public class DebitNoteServiceImpl implements DebitNoteService {

    private final DebitNoteRepository debitNoteRepository;

    @Override
    public DebitNote reverseDebitNote(ReversalRequestDTO request) {
        DebitNote note = debitNoteRepository.findById(request.getDebitNoteId())
                .orElseThrow(() -> new RuntimeException("Debit Note not found"));

        if (note.getStatus() == DebitNoteStatus.REVERSED) {
            throw new IllegalStateException("Debit Note has already been reversed");
        }

        note.setStatus(DebitNoteStatus.REVERSED);
        note.setReversalReason(request.getReason());
        note.setReversedBy(request.getCreatedBy());
        note.setReversedAt(LocalDateTime.now());

        return debitNoteRepository.save(note);
    }

    public DebitNote linkNewDebitNoteToOld(Long oldNoteId, DebitNote newNotePayload, String createdBy) {
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

        return debitNoteRepository.save(newNote);
    }
}
