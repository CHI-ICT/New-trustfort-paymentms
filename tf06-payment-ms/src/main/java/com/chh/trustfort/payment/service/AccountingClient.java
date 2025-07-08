package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.dto.JournalEntryRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "accountingClient", url = "${ACCOUNTING_SERVICE_URL}")
public interface AccountingClient {

    @PostMapping(value = "/internal/journal-entry", consumes = MediaType.APPLICATION_JSON_VALUE)
    String postJournalEntryInternal(@RequestBody JournalEntryRequest request);
}
