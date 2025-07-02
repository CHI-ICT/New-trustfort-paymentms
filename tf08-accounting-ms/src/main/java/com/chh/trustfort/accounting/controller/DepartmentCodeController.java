package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.Quintuple;
import com.chh.trustfort.accounting.Util.SecureResponseUtil;
import com.chh.trustfort.accounting.component.RequestManager;
import com.chh.trustfort.accounting.component.Role;
import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.DepartmentCode;
import com.chh.trustfort.accounting.payload.DepartmentCodeRequestDTO;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.DepartmentCodeService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Tag(name = "Department Codes", description = "Manage department classification codes")
@Slf4j
public class DepartmentCodeController {

    private final DepartmentCodeService service;
    private final RequestManager requestManager;
    private final AesService aesService;
    private final Gson gson;

    @PostMapping(value = ApiPath.CREATE_DEPARTMENT_CODE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestParam String idToken, @RequestBody String requestPayload, HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.DEPARTMENT_CODE.getValue(), requestPayload, httpRequest, idToken);
       if (request.isError) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
        SecureResponseUtil.error("Unauthorized request", "401", "FAILED")
    );
}
        DepartmentCodeRequestDTO dto = gson.fromJson(request.payload, DepartmentCodeRequestDTO.class);
        DepartmentCode created = service.create(dto);
        return ResponseEntity.ok(aesService.encrypt(gson.toJson(created), request.appUser));
    }

    @PutMapping(value = ApiPath.UPDATE_DEPARTMENT_CODE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@RequestParam String idToken, @RequestBody String requestPayload, HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.DEPARTMENT_CODE.getValue(), requestPayload, httpRequest, idToken);

        if (request.isError) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    SecureResponseUtil.error("Unauthorized request", "401", "FAILED")
            );
        }

            DepartmentCodeRequestDTO dto = gson.fromJson(request.payload, DepartmentCodeRequestDTO.class);
        String code = gson.fromJson(request.payload, JsonObject.class).get("code").getAsString();

        DepartmentCode updated = service.update(code, dto);
        return ResponseEntity.ok(aesService.encrypt(gson.toJson(updated), request.appUser));
    }

    @GetMapping(value = ApiPath.ALL_DEPARTMENT_CODES, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAll(@RequestParam String idToken, HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.DEPARTMENT_CODE.getValue(), "", httpRequest, idToken);
        if (request.isError) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    SecureResponseUtil.error("Unauthorized request", "401", "FAILED")
            );
        }

        List<DepartmentCode> list = service.findAll();
        return ResponseEntity.ok(aesService.encrypt(gson.toJson(list), request.appUser));
    }

    @GetMapping(value = ApiPath.GET_DEPARTMENT_CODE_BY_CODE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getByCode(@RequestParam String idToken, @RequestParam String code, HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.DEPARTMENT_CODE.getValue(), "", httpRequest, idToken);
        if (request.isError) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    SecureResponseUtil.error("Unauthorized request", "401", "FAILED")
            );
        }

        Optional<DepartmentCode> result = service.findByCode(code);
        return result.map(dc -> ResponseEntity.ok(aesService.encrypt(gson.toJson(dc), request.appUser)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Code not found"));
    }

    @GetMapping(value = ApiPath.DELETED_DEPARTMENT_CODES, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDeleted(@RequestParam String idToken, HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.DEPARTMENT_CODE.getValue(), "", httpRequest, idToken);
        if (request.isError) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    SecureResponseUtil.error("Unauthorized request", "401", "FAILED")
            );
        }

        List<DepartmentCode> list = service.findAllDeleted();
        return ResponseEntity.ok(aesService.encrypt(gson.toJson(list), request.appUser));
    }

    @PatchMapping(value = ApiPath.DELETE_DEPARTMENT_CODE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> softDelete(@RequestParam String idToken, @RequestBody String requestPayload, HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.DEPARTMENT_CODE.getValue(), requestPayload, httpRequest, idToken);
        if (request.isError) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    SecureResponseUtil.error("Unauthorized request", "401", "FAILED")
            );
        }

        String code = gson.fromJson(request.payload, JsonObject.class).get("code").getAsString();
        String response = service.delete(code);
        return ResponseEntity.ok(aesService.encrypt(gson.toJson(response), request.appUser));
    }

    @PutMapping(value = ApiPath.RESTORE_DEPARTMENT_CODE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> restore(@RequestParam String idToken, @RequestBody String requestPayload, HttpServletRequest httpRequest) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.DEPARTMENT_CODE.getValue(), requestPayload, httpRequest, idToken);
        if (request.isError) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    SecureResponseUtil.error("Unauthorized request", "401", "FAILED")
            );
        }

        String code = gson.fromJson(request.payload, JsonObject.class).get("code").getAsString();
        DepartmentCode restored = service.restore(code);
        return ResponseEntity.ok(aesService.encrypt(gson.toJson(restored), request.appUser));
    }
}
