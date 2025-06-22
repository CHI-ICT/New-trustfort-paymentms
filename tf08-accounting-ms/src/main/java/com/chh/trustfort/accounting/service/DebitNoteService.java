package com.chh.trustfort.accounting.service;// DebitNoteService.java

import com.chh.trustfort.accounting.dto.ReversalRequestDTO;
import com.chh.trustfort.accounting.model.DebitNote;

public interface DebitNoteService {
    DebitNote reverseDebitNote(ReversalRequestDTO request);

    DebitNote linkNewDebitNoteToOld(Long oldNoteId, DebitNote newNotePayload, String createdBy);

}