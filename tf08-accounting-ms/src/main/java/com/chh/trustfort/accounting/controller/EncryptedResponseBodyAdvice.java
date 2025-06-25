//package com.chh.trustfort.accounting.controller;
//
//
//import com.chh.trustfort.accounting.Responses.EncryptResponse;
//import com.chh.trustfort.accounting.security.AesService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.core.MethodParameter;
//import org.springframework.http.MediaType;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
//
//import java.lang.reflect.AnnotatedElement;
//import java.util.HashMap;
//import java.util.Map;
//
//@ControllerAdvice
//@RequiredArgsConstructor
//@Slf4j
//public class EncryptedResponseBodyAdvice implements ResponseBodyAdvice<Object> {
//
//    private final AesService aesService;
//    private final ObjectMapper objectMapper;
//
//    private static final String ENCRYPTION_KEY = "99A47258y83921B1627495826M729361/1234567890123456";
//
//    @Override
//    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
//        AnnotatedElement element = returnType.getContainingClass();
//        if (element.isAnnotationPresent(EncryptResponse.class)) return true;
//        return returnType.getMethodAnnotation(EncryptResponse.class) != null;
//    }
//
//    @Override
//    public Object beforeBodyWrite(Object body,
//                                  MethodParameter returnType,
//                                  MediaType selectedContentType,
//                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
//                                  org.springframework.http.server.ServerHttpRequest request,
//                                  org.springframework.http.server.ServerHttpResponse response) {
//
//        try {
//            // Handle nulls safely
//            if (body == null) {
//                return aesService.encrypt("null", ENCRYPTION_KEY);
//            }
//
//            // Wrap primitive/String types to ensure serialization
//            Object toSerialize = body;
//            if (body instanceof String || body.getClass().isPrimitive()) {
//                Map<String, Object> wrapper = new HashMap<>();
//                wrapper.put("value", body);
//                toSerialize = wrapper;
//            }
//
//            String json = objectMapper.writeValueAsString(toSerialize);
//            return aesService.encrypt(json, ENCRYPTION_KEY);
//
//        } catch (Exception ex) {
//            log.error("Failed to encrypt response: {}", ex.getMessage(), ex);
//            throw new RuntimeException("Encryption failed", ex);
//        }
//    }
//}
