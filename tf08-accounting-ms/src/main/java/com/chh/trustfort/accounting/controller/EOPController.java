package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.ApiResponse;
import com.chh.trustfort.accounting.dto.EOPRequestDTO;
import com.chh.trustfort.accounting.dto.EOPResponseDTO;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.EOPService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

// EOPController.java
@RestController
@RequestMapping(ApiPath.BASE_API)
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Evidence of Payment", description = "Generate and retrieve EOP")
public class EOPController {

    private final EOPService eopService;
    private final RequestManager requestManager;
    private final AesService aesService;
    private final Gson gson;

    @PostMapping(value = ApiPath.GENERATE_EOP, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generateEOP(@RequestParam String idToken, @RequestParam Long invoiceId,
                                         @RequestBody String requestPayload, HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.GENERATE_EOP.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.badRequest().body(aesService.encrypt(SecureResponseUtil.error(
                    response.getResponseMessage(), response.getResponseCode(), "fail"), request.appUser));
        }

        EOPRequestDTO dto = gson.fromJson(request.payload, EOPRequestDTO.class);
        EOPResponseDTO eop = eopService.generateEOP(invoiceId, dto);

        return ResponseEntity.ok().body(aesService.encrypt(
                SecureResponseUtil.success("EOP generated successfully", eop), request.appUser));
    }

    @GetMapping(value = ApiPath.GET_EOP, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getEOP(@RequestParam String idToken, @RequestParam Long invoiceId,
                                    HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.GET_EOP.getValue(), "", httpRequest, idToken
        );

        if (request.isError) {
            return ResponseEntity.badRequest().body(aesService.encrypt(
                    SecureResponseUtil.error("Unauthorized", "01", "fail"), request.appUser));
        }

        Optional<EOPResponseDTO> result = eopService.getEOPByInvoiceId(invoiceId);
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(aesService.encrypt(
                    SecureResponseUtil.error("No EOP found for this invoice", "04", "fail"), request.appUser));
        }

        return ResponseEntity.ok().body(aesService.encrypt(
                SecureResponseUtil.success("EOP fetched successfully", result.get()), request.appUser));
    }
}
