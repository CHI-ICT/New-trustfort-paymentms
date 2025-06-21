package com.chh.trustfort.payment.controller.facility;

import com.chh.trustfort.payment.Quintuple;
import com.chh.trustfort.payment.Util.SecureResponseUtil;
import com.chh.trustfort.payment.component.RequestManager;
import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.dto.CreditLineRequestDto;
import com.chh.trustfort.payment.dto.CreditLineResponseDto;
import com.chh.trustfort.payment.enums.Role;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.payload.OmniResponsePayload;
import com.chh.trustfort.payment.security.AesService;
import com.chh.trustfort.payment.service.facility.CreditLineService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class CreditLineController {

    @Autowired
    private CreditLineService creditLineService;

    @Autowired
    private RequestManager requestManager;

    @Autowired
    private AesService aesService;

    @Autowired
    private Gson gson;

    @PostMapping(value = ApiPath.CREATE_CREDIT_LINE, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(String idToken, @RequestBody String requestPayload, HttpServletRequest httpRequest)  {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.CREATE_CREDIT_LINE.getValue(), requestPayload, httpRequest, idToken);
        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)), HttpStatus.OK);
        }

        CreditLineRequestDto dto = gson.fromJson(request.payload, CreditLineRequestDto.class);
        creditLineService.create(dto);
        return new ResponseEntity<>(aesService.encrypt("Request Created", request.appUser), HttpStatus.OK);
    }

    @GetMapping(value = ApiPath.GET_ALL_CREDIT_LINES, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAll(String idToken, HttpServletRequest httpRequest){

        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.VIEW_CREDIT_LINE.getValue(), null, httpRequest, idToken);
        if(request.isError){
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);

            return new ResponseEntity<>(aesService.encrypt(SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.UNAUTHORIZED)),request.appUser) , HttpStatus.OK);
        }
        return new ResponseEntity<>(aesService.encrypt(gson.toJson(creditLineService.getAll()), request.appUser), HttpStatus.OK);
    }

    @GetMapping(value = ApiPath.GET_CREDIT_LINE_BY_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getById(String idToken, HttpServletRequest httpRequest, @PathVariable Long id){
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.VIEW_CREDIT_LINE.getValue(),null, httpRequest, idToken);

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(aesService.encrypt(SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.UNAUTHORIZED)),request.appUser) , HttpStatus.OK);
        }

        return new ResponseEntity<>(aesService.encrypt(gson.toJson(creditLineService.getById(id)), request.appUser), HttpStatus.OK);

    }

    @PutMapping(value = ApiPath.UPDATE_CREDIT_LINE, consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> update(String idToken, @RequestBody String requestPayload, HttpServletRequest httpRequest, @PathVariable Long id) {
        Quintuple<Boolean, String, String, AppUser, String> request = requestManager.validateRequest(Role.UPDATE_CREDIT_LINE.getValue(), requestPayload, httpRequest, idToken);

        if (request.isError) {
            OmniResponsePayload response = gson.fromJson(request.payload, OmniResponsePayload.class);
            return new ResponseEntity<>(SecureResponseUtil.error(response.getResponseCode(), response.getResponseMessage(), String.valueOf(HttpStatus.BAD_REQUEST)), HttpStatus.OK);
        }

        CreditLineRequestDto dto = gson.fromJson(request.payload, CreditLineRequestDto.class);
        CreditLineResponseDto updated = creditLineService.update(id, dto);

        return new ResponseEntity<>(aesService.encrypt(gson.toJson(updated), request.appUser), HttpStatus.OK);
    }
}
