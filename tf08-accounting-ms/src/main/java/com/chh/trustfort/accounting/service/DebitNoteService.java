package com.chh.trustfort.accounting.service;// DebitNoteService.java

import com.chh.trustfort.accounting.dto.ReversalRequestDTO;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.DebitNote;

public interface DebitNoteService {
    String reverseDebitNote(ReversalRequestDTO request, AppUser appUser);

    String linkNewDebitNoteToOld(Long oldNoteId, DebitNote newNotePayload, String createdBy, AppUser appUser);

}