package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.model.AccountCategory;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.payload.AccountCategoryRequest;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.AccountCategoryService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Account Categories", description = "Manage COA Account Categories")
public class AccountCategoryController {

    private final AccountCategoryService service;
    private final AesService aesService;
    private final Gson gson;
    private final RequestManager requestManager;

//    @PostMapping(value = ApiPath.ENCRYPT_PAYLOAD, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> encryptTestPayload(
//            @RequestBody AccountCategoryRequest requestPayload,
//            @RequestHeader("idToken") String idToken,
//            HttpServletRequest httpRequest) {
//
//        // Use validateRequest to extract AppUser
//        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
//                Role.ACCOUNT_CATEGORY.getValue(), null, httpRequest, idToken
//        );
//
//        if (request.isError || request.appUser == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid authorization.");
//        }
//
//        AppUser appUser = request.appUser;
//
//        // Encrypt the raw payload (no decryption step needed)
//        String encrypted = aesService.encrypt(gson.toJson(requestPayload), appUser);
//
//        log.info("🔐 Encrypted payload: {}", encrypted);
//        return ResponseEntity.ok(Map.of("encryptedPayload", encrypted));
//    }

    @PostMapping(value = ApiPath.ENCRYPT_PAYLOAD, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> encryptGenericPayload(
            @RequestBody String rawJsonPayload,
            @RequestHeader("idToken") String idToken,
            HttpServletRequest httpRequest) {

        log.info("🔐 Received payload for encryption: {}", rawJsonPayload);

        // Validate and extract user
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.ACCOUNT_CATEGORY.getValue(), null, httpRequest, idToken
        );

        if (request.isError || request.appUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "fail", "message", "Invalid authorization."));
        }

        AppUser appUser = request.appUser;

        // Encrypt the plain JSON
        String encrypted = aesService.encrypt(rawJsonPayload, appUser);

        return ResponseEntity.ok(Map.of("encryptedPayload", encrypted));
    }

    @PostMapping(value = ApiPath.CREATE_ACCOUNT_CATEGORY, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCategory(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody String requestPayload,
            HttpServletRequest httpRequest) {

        String idToken = authorizationHeader.replace("Bearer ", "").trim();
        log.info("🔐 ID TOKEN: {}", idToken);
        log.info("📥 RAW ENCRYPTED PAYLOAD: {}", requestPayload);

        // 🔒 Validate, decrypt & extract user info
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.ACCOUNT_CATEGORY.getValue(), requestPayload, httpRequest, idToken
        );
        request.appUser.setIpAddress(httpRequest.getRemoteAddr());

        // 🔁 Handle decryption or role error
        if (request.isError) {
            String decrypted = aesService.decrypt(request.payload, request.appUser);
            OmniResponsePayload errorResponse = gson.fromJson(decrypted, OmniResponsePayload.class);
            return new ResponseEntity<>(
                    SecureResponseUtil.error(
                            errorResponse.getResponseCode(),
                            errorResponse.getResponseMessage(),
                            String.valueOf(HttpStatus.BAD_REQUEST)),
                    HttpStatus.OK
            );
        }

        // ✅ Decrypt the payload
        log.info("📥 Decrypted Payload: {}", request.payload);
        AccountCategoryRequest decryptedPayload = gson.fromJson(request.payload, AccountCategoryRequest.class);

        // 🛠️ Call the service and return encrypted result
        String result = service.createCategory(decryptedPayload, request.appUser);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(value = ApiPath.GET_ACCOUNT_CATEGORIES, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllCategories(
            @RequestParam String idToken,
            HttpServletRequest httpRequest) {

        log.info("🔐 GET CATEGORIES TOKEN: {}", idToken);
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
                Role.ACCOUNT_CATEGORY.getValue(), null, httpRequest, idToken
        );

        request.appUser.setIpAddress(httpRequest.getRemoteAddr());

        if (request.isError) {
            return new ResponseEntity<>(
                    SecureResponseUtil.error("99", "Invalid token", String.valueOf(HttpStatus.UNAUTHORIZED)),
                    HttpStatus.OK
            );
        }

        String result = service.getAllCategories(request.appUser);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


@GetMapping(value = ApiPath.GET_ACCOUNT_CATEGORY_BY_ID, produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<?> getCategoryById(
        @RequestParam String idToken,
        @RequestParam Long id,
        HttpServletRequest httpRequest) {

    Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
            Role.ACCOUNT_CATEGORY.getValue(), null, httpRequest, idToken
    );
    request.appUser.setIpAddress(httpRequest.getRemoteAddr());

    if (request.isError) {
        return new ResponseEntity<>(
                SecureResponseUtil.error("99", "Unauthorized", String.valueOf(HttpStatus.UNAUTHORIZED)),
                HttpStatus.OK
        );
    }

    String result = service.getCategoryById(id, request.appUser);
    return new ResponseEntity<>(result, HttpStatus.OK);
}


@PutMapping(value = ApiPath.UPDATE_ACCOUNT_CATEGORY, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<?> updateCategory(
        @RequestHeader("Authorization") String authorizationHeader,
        @RequestBody String requestPayload,
        HttpServletRequest httpRequest) {

    String idToken = authorizationHeader.replace("Bearer ", "").trim();
    log.info("🔐 ID TOKEN: {}", idToken);
    log.info("📥 RAW PAYLOAD (Base64): {}", requestPayload);

    Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(
            Role.ACCOUNT_CATEGORY.getValue(), requestPayload, httpRequest, idToken
    );
    request.appUser.setIpAddress(httpRequest.getRemoteAddr());

    if (request.isError) {
        String decrypted = aesService.decrypt(request.payload, request.appUser);
        OmniResponsePayload response = gson.fromJson(decrypted, OmniResponsePayload.class);
        return new ResponseEntity<>(
                SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)),
                HttpStatus.OK
        );
    }

    log.info("📥 Decrypted Payload: {}", request.payload);
    AccountCategoryRequest decryptedPayload = gson.fromJson(request.payload, AccountCategoryRequest.class);
    Long categoryId = decryptedPayload.getId(); // Ensure your request includes ID

    String result = service.updateCategory(categoryId, decryptedPayload, request.appUser);
    return new ResponseEntity<>(result, HttpStatus.OK);
}


//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        try {
//            service.deleteCategory(id);
//            return ResponseEntity.noContent().build();
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.notFound().build();
//        }
//    }
}
