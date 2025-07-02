package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.PurchaseOrder;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.serviceImpl.PurchaseOrderServiceImpl;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Purchase Order", description = "Manage Purchase Orders")
public class PurchaseOrderController {

    private final PurchaseOrderServiceImpl purchaseOrderService;
    private final RequestManager requestManager;
    private final AesService aesService;
    private final Gson gson;

    @PostMapping(value = ApiPath.CREATE_PO, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createPO(@RequestParam String idToken, @RequestBody String requestPayload, HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.CREATE_PURCHASE_ORDER.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(request.payload);
        }

        PurchaseOrder po = gson.fromJson(request.payload, PurchaseOrder.class);
        String encryptedResponse = purchaseOrderService.createPurchaseOrder(po, request.appUser);
        return ResponseEntity.ok(encryptedResponse);
    }

    @GetMapping(value = ApiPath.ALL_PO, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllPOs(@RequestParam String idToken, HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.VIEW_PURCHASE_ORDER.getValue(), "", httpRequest, idToken
        );

        if (request.isError) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(request.payload);
        }

        String encryptedResponse = purchaseOrderService.getAllPurchaseOrders(request.appUser);
        return ResponseEntity.ok(encryptedResponse);
    }
}
