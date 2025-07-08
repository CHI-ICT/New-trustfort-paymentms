//package com.chh.trustfort.payment.security;
//
//import com.chh.trustfort.payment.Config.CustomFeignSslConfig;
//import com.chh.trustfort.payment.dto.JournalEntryRequest;
//import com.chh.trustfort.payment.payload.OmniResponsePayload;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//
//@FeignClient(
//    name = "trustfort-accounting", // the name registered in Eureka
//    url = "${ACCOUNTING_SERVICE_URL:https://localhost:8445/trustfort/api/v1/accountingService}", // override for local
//    configuration = CustomFeignSslConfig.class // optional, for SSL trust issues
//)
//public interface AccountingClient {
//
//    @PostMapping(value = "/journal-entry", consumes = MediaType.APPLICATION_JSON_VALUE)
//    OmniResponsePayload postJournalEntry(@RequestBody JournalEntryRequest request);
//}
