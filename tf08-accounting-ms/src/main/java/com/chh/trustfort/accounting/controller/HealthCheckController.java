package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.model.SkipEncryption;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SkipEncryption
public class HealthCheckController {
    // this will not be encrypted
}
