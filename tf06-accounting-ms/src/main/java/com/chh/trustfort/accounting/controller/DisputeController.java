package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.CreateDisputeRequest;
import com.chh.trustfort.accounting.dto.DisputeResolutionRequest;
import com.chh.trustfort.accounting.dto.DisputeResponse;
import com.chh.trustfort.accounting.service.DisputeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RequestMapping(ApiPath.BASE_API)
@Slf4j
public class DisputeController {

    private final DisputeService disputeService;

    @PostMapping(value = ApiPath.RAISE_DISPUTE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DisputeResponse> raiseDispute(@RequestBody CreateDisputeRequest request) {
        log.info("Raising new dispute for receipt: {}", request.getRelatedReceiptReference());
        DisputeResponse response = disputeService.raiseDispute(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = ApiPath.RESOLVE_DISPUTE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resolveDispute(@RequestBody DisputeResolutionRequest request) {
        disputeService.resolveDispute(request.getReference(), request.getResolution(), request.getResolvedBy());
        return ResponseEntity.ok("Dispute resolved successfully");
    }

    @GetMapping(value = ApiPath.GET_DISPUTES)
    public ResponseEntity<List<DisputeResponse>> getAllDisputes() {
        log.info("Fetching all disputes...");
        return ResponseEntity.ok(disputeService.getAllDisputes());
    }
}
