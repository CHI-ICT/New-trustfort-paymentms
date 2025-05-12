package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.BalanceSheetFilterRequest;
import com.chh.trustfort.accounting.dto.BalanceSheetResponse;
import com.chh.trustfort.accounting.dto.JournalEntryRequest;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.Users;
import com.chh.trustfort.accounting.service.BalanceSheetService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Tag(name = "Financial Statements", description = "Handles Balance Sheet generation")
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(ApiPath.BASE_API)
@Slf4j
public class BalanceSheetController {

    @Autowired
    private final RequestManager requestManager;

    @Autowired
    private final Gson gson;


    private Users users;

    @Autowired
    private final BalanceSheetService balanceSheetService;

    @GetMapping(value = ApiPath.GENERATE_BALANCE_SHEET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generateBalanceSheet(@RequestBody BalanceSheetFilterRequest payload, HttpServletRequest httpRequest) throws Exception {

        Quintuple<Boolean, String, String, Users, String> request = requestManager.validateRequest(
                Role.GENERATE_BALANCE_SHEET.getValue(),
                "Generate Balance Sheet as of: " + payload,
                httpRequest,
                ApiPath.ID_TOKEN
        );

        if (request.isError) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(request.payload);
        }

        Users user = request.Users;
        if (user == null) {
            log.error("🔒 User not found during balance sheet request");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
        }

        log.info("📄 Balance sheet generation requested by {}", user.getUserName());

        BalanceSheetFilterRequest filter = gson.fromJson(request.payload, BalanceSheetFilterRequest.class);
        BalanceSheetResponse response = balanceSheetService.generateBalanceSheet(filter);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }



}
