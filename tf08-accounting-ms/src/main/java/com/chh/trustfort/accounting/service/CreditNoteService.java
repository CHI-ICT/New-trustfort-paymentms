package com.chh.trustfort.accounting.service;// CreditNoteService.java

import com.chh.trustfort.accounting.dto.CreditNoteRequestDTO;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.CreditNote;

public interface CreditNoteService {
    String createCreditNote(CreditNoteRequestDTO request, AppUser appUser);
}