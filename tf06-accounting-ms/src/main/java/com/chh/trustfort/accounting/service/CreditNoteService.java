package com.chh.trustfort.accounting.service;// CreditNoteService.java

import com.chh.trustfort.accounting.dto.CreditNoteRequestDTO;
import com.chh.trustfort.accounting.model.CreditNote;

public interface CreditNoteService {
    CreditNote createCreditNote(CreditNoteRequestDTO request);
}