package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.EntityCode;
import com.chh.trustfort.accounting.payload.EntityCodeRequest;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.EntityCodeService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EntityCodeController {

    private final EntityCodeService service;
    private final RequestManager requestManager;
    private final AesService aesService;
    private final Gson gson;

    @PostMapping(value = ApiPath.CREATE_ENTITY_CODE, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createEntityCode(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest
    ) {
        String idToken = authorizationHeader.replace("Bearer ", "").trim();
        log.info("🔐 ID TOKEN: {}", idToken);
        log.info("📥 RAW ENCRYPTED PAYLOAD: {}", requestPayload);

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.CREATE_ENTITY_CODE.getValue(), requestPayload, httpRequest, idToken
        );

        AppUser appUser = request.appUser;
        appUser.setIpAddress(httpRequest.getRemoteAddr());

        if (request.isError) {
            String decryptedError = aesService.decrypt(request.payload, appUser);
            OmniResponsePayload error = gson.fromJson(decryptedError, OmniResponsePayload.class);
            return ResponseEntity.ok(aesService.encrypt(
                    SecureResponseUtil.error(error.getResponseMessage(), error.getResponseCode(), "fail"), appUser
            ));
        }

        log.info("📥 Decrypted Payload: {}", request.payload);
        EntityCodeRequest dto = gson.fromJson(request.payload, EntityCodeRequest.class);

        String created = service.create(dto, appUser);
        return ResponseEntity.ok(aesService.encrypt(
                SecureResponseUtil.success("Entity code created successfully", created), appUser
        ));
    }


    @GetMapping(value = ApiPath.GET_ENTITY_CODES, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll(@RequestParam String idToken, HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.GET_ENTITY_CODES.getValue(), "", httpRequest, idToken
        );

        if (request.isError) {
            return ResponseEntity.badRequest().body(
                    aesService.encrypt(SecureResponseUtil.error("Unauthorized", "01", "fail"), request.appUser)
            );
        }

        List<EntityCode> result = service.getAll();
        return ResponseEntity.ok().body(
                aesService.encrypt(SecureResponseUtil.success("Entity codes fetched successfully", result), request.appUser)
        );
    }

    @GetMapping(value = ApiPath.GET_ENTITY_CODE_BY_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getById(@RequestParam String idToken, @RequestParam Long id, HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.GET_ENTITY_CODES.getValue(), "", httpRequest, idToken
        );

        if (request.isError) {
            return ResponseEntity.badRequest().body(
                    aesService.encrypt(SecureResponseUtil.error("Unauthorized", "01", "fail"), request.appUser)
            );
        }

        Optional<EntityCode> entity = service.getById(id);
        if (entity.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    aesService.encrypt(SecureResponseUtil.error("Entity not found", "04", "fail"), request.appUser)
            );
        }

        return ResponseEntity.ok().body(
                aesService.encrypt(SecureResponseUtil.success("Entity code fetched", entity.get()), request.appUser)
        );
    }

    @PutMapping(value = ApiPath.UPDATE_ENTITY_CODE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@RequestParam String idToken, @RequestParam Long id, @RequestBody String requestPayload, HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.UPDATE_ENTITY_CODE.getValue(), requestPayload, httpRequest, idToken
        );

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return ResponseEntity.badRequest().body(
                    aesService.encrypt(SecureResponseUtil.error(response.getResponseMessage(), response.getResponseCode(), "fail"), request.appUser)
            );
        }

        EntityCodeRequest dto = gson.fromJson(request.payload, EntityCodeRequest.class);
        EntityCode updated = service.update(id, dto);

        return ResponseEntity.ok().body(
                aesService.encrypt(SecureResponseUtil.success("Entity code updated", updated), request.appUser)
        );
    }

    @DeleteMapping(value = ApiPath.DELETE_ENTITY_CODE)
    public ResponseEntity<?> delete(@RequestParam String idToken, @RequestParam Long id, HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.DELETE_ENTITY_CODE.getValue(), "", httpRequest, idToken
        );

        if (request.isError) {
            return ResponseEntity.badRequest().body(
                    aesService.encrypt(SecureResponseUtil.error("Unauthorized", "01", "fail"), request.appUser)
            );
        }

        service.delete(id);
        return ResponseEntity.ok().body(
                aesService.encrypt(SecureResponseUtil.success("Entity code deleted", null), request.appUser)
        );
    }
}
