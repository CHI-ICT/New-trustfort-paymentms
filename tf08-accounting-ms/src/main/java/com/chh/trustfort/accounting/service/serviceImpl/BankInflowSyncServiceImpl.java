package com.chh.trustfort.accounting.service.serviceImpl;

import com.chh.trustfort.accounting.dto.BankInflowPayload;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.BankInflowSyncService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankInflowSyncServiceImpl implements BankInflowSyncService {

    private final AesService aesService;
    private final Gson gson;
    private final MessageSource messageSource;

    @Override
    public String syncInflow(BankInflowPayload payload, AppUser appUser) {
        OmniResponsePayload response = new OmniResponsePayload();
        response.setResponseCode("06");
        response.setResponseMessage("Failed to sync bank inflow");

        if (payload == null || payload.getReference() == null || payload.getAmount() == null) {
            response.setResponseMessage("Missing required fields");
            return aesService.encrypt(gson.toJson(response), appUser);
        }

        log.info("💳 Received bank inflow sync request: {}", gson.toJson(payload));

        response.setResponseCode("00");
        response.setResponseMessage(messageSource.getMessage("bank.inflow.synced", null, Locale.ENGLISH));
//        response.setData(Map.of(
//                "reference", payload.getReference(),
//                "status", "SYNCHRONIZED"
//        )
//        );

        return aesService.encrypt(gson.toJson(response), appUser);
    }
}

