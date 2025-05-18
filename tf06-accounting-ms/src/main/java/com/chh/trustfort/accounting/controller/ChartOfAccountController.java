// ==== CONTROLLER: ChartOfAccountController.java ====
package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.ChartOfAccountRequest;
import com.chh.trustfort.accounting.dto.ChartOfAccountResponse;
import com.chh.trustfort.accounting.service.ChartOfAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.chh.trustfort.accounting.constant.ApiPath.*;

@RestController
@RequiredArgsConstructor
//@RequestMapping(BASE_API + COA_BASE)
@RequestMapping(ApiPath.BASE_API)
public class ChartOfAccountController {

    private final ChartOfAccountService service;


    @PostMapping(value = CREATE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChartOfAccountResponse> create(@Valid @RequestBody ChartOfAccountRequest request) {
        return ResponseEntity.ok(service.createChartOfAccount(request));
    }


    @GetMapping(value = GET_ALL)
    public ResponseEntity<List<ChartOfAccountResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
}

