package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.CreateReceivableRequest;
import com.chh.trustfort.accounting.dto.ReceivableRequest;
import com.chh.trustfort.accounting.model.Receivable;
import com.chh.trustfort.accounting.service.ReceivableService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(ApiPath.BASE_API)
@Slf4j
public class ReceivableController {

    private final ReceivableService receivableService;

    @PostMapping(value = ApiPath.CREATE_RECEIVABLE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Receivable> createReceivable(@Validated @RequestBody CreateReceivableRequest request) {
        Receivable receivable = receivableService.createReceivable(request);
        return ResponseEntity.ok(receivable);
    }

    @GetMapping(ApiPath.GET_RECEIVABLE)
    public ResponseEntity<List<Receivable>> getAllReceivables() {
        log.info("Fetching all receivables");
        return ResponseEntity.ok(receivableService.getAllReceivables());
    }
}
