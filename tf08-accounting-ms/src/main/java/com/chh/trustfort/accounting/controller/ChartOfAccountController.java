// ==== CONTROLLER: ChartOfAccountController.java ====
package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Responses.EncryptResponse;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.model.ChartOfAccount;
import com.chh.trustfort.accounting.payload.CreateCOARequestPayload;
import com.chh.trustfort.accounting.service.ChartOfAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.chh.trustfort.accounting.constant.ApiPath.*;

@RestController
@EncryptResponse
@RequiredArgsConstructor
@RequestMapping(ApiPath.BASE_API)
public class ChartOfAccountController {

    private final ChartOfAccountService service;


    @PostMapping(value = CREATE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChartOfAccount> create(@Valid @RequestBody CreateCOARequestPayload request) {
        return ResponseEntity.ok(service.createAccount(request));
    }


    @GetMapping(value = GET_ALL)
    public ResponseEntity<List<ChartOfAccount>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }
}

