//package com.chh.trustfort.accounting.controller;
//
//import com.chh.trustfort.accounting.Responses.EncryptedResponse;
//import com.chh.trustfort.accounting.Responses.ResponseEncryptionUtil;
//import com.chh.trustfort.accounting.model.SkipEncryption;
//import lombok.RequiredArgsConstructor;
//import org.springframework.core.MethodParameter;
//import org.springframework.http.MediaType;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
//
//@RestControllerAdvice
//@RequiredArgsConstructor
//public class GlobalEncryptionAdvice implements ResponseBodyAdvice<Object> {
//
//    private final ResponseEncryptionUtil responseEncryptionUtil;
//
//    @Override
//    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
//        // Exclude Swagger, error handling paths, etc.
//        return !returnType.getDeclaringClass().getName().contains("springdoc")
//               && !returnType.getContainingClass().isAnnotationPresent(SkipEncryption.class);
//    }
//
//    @Override
//    public Object beforeBodyWrite(Object body, MethodParameter returnType,
//                                  MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
//                                  ServerHttpRequest request, ServerHttpResponse response) {
//
//        // Don't encrypt already encrypted payloads or error responses
//        if (body instanceof EncryptedResponse || body instanceof String || selectedContentType != MediaType.APPLICATION_JSON) {
//            return body;
//        }
//
//        String encrypted = responseEncryptionUtil.encrypt(body);
//        return new EncryptedResponse(encrypted);
//    }
//}
