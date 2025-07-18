package com.chh.trustfort.payment.service;

import com.chh.trustfort.payment.jwt.JwtTokenUtil;
import com.chh.trustfort.payment.model.AppUser;
import com.chh.trustfort.payment.model.Users;
import com.chh.trustfort.payment.payload.PaymentReferenceRequestPayload;
import com.chh.trustfort.payment.payload.QuoteRequestPayload;
import com.chh.trustfort.payment.security.AesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Daniel Ofoleta
 */
@Service
public class PaymentService {

    @Autowired
    JwtTokenUtil jwtToken;

    @Autowired
    AesService aesService;

    Logger logger = LoggerFactory.getLogger(PaymentService.class);

    public String generateQuote(Users users, QuoteRequestPayload requestPayload) {

        return "";
    }

    public String processPayment(Users users, PaymentReferenceRequestPayload requestPayload) {

        return "";
    }

}
