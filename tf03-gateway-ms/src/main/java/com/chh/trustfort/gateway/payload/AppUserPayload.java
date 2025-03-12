/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.chh.trustfort.gateway.payload;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author dofoleta
 */
@Data
public class AppUserPayload {
    private Long id;
    private String userName;
    private String password;
    private String createdBy;
    private String createdAt;
    private String updatedBy;
    private String updatedAt = null;
    private boolean isLocked = false;
    private boolean isExpired = false;
    private boolean isEnabled = true;
    private String channel;
    private String passwordChangeDate = null;
    private String encryptionKey;
    private boolean payAccountOpenBonus = false;
    private BigDecimal accountOpenBonus = BigDecimal.ZERO;
    private String accountNumber = "";
    private boolean authenticatDevice = false;
    private String tcred;
    private String role;

    private String token;
    private String responseCode;
    private String responseMessage;
    private BigDecimal twofactorAmount;

//    private Long id;
//    private String userName;
//    private String password;
//    private String createdBy;
//    private LocalDateTime createdAt = LocalDateTime.now();
//    private String updatedBy;
//    private LocalDateTime updatedAt = null;
//    private boolean isLocked = false;
//    private boolean isExpired = false;
//    private boolean isEnabled = true;
//    private String channel;
//    private LocalDate passwordChangeDate = null;
//    private String encryptionKey;
//    private boolean payAccountOpenBonus = false;
//    private BigDecimal accountOpenBonus = BigDecimal.ZERO;
//    private String accountNumber = "";
//    private boolean authenticatDevice = false;
//    private String tcred;
//    private String role;
//    
//    private String token;
//    private String responseCode;
//    private String responseMessage;
//    private BigDecimal twofactorAmount;
}
