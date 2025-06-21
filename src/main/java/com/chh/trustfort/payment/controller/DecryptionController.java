//package com.chh.trustfort.payment.controller;
//
//
//import com.chh.trustfort.payment.constant.ApiPath;
//import com.chh.trustfort.payment.security.AesService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping(ApiPath.BASE_API)
//@RequiredArgsConstructor
//@Slf4j
//public class DecryptionController {
//
//    private final AesService aesService;
//
//
//    @PostMapping(value = ApiPath.DECRYPT,produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<String> decrypt(
//            @RequestParam String cipherText,
//            @RequestParam String keyWithIv
//    ) {
//        try {
//            String decrypted = aesService.decrypt(cipherText, keyWithIv);
//            return ResponseEntity.ok(decrypted);
//        } catch (Exception e) {
//            log.error("Decryption failed: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Decryption failed: " + e.getMessage());
//        }
//    }
//}
