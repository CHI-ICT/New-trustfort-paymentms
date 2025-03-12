package com.chh.trustfort.gateway.payload.user;

import javax.validation.constraints.*;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 *
 * @author dofoleta
 */
@Data
public class LoginRequestPayload {

    @NotBlank(message = "User name cannot be blanck.")
    @NotNull(message = "User name cannot be null.")
    @Size(min = 11, max = 11, message = "User name must be exactly 11 characters.")
    private String userName;

    @NotNull(message = "Passcode cannot be null.")
    @NotBlank(message = "Passcode cannot be blank.")
    @Size(min = 6, max = 6, message = "Passcode must be exactly 6 characters.")
    @Pattern(regexp = "^[0-9]{6}$", message = "Passcode must contain only numeric characters.")
    private String passcode;
    
    private String deviceId;
    
    @NotNull(message = "Passcode cannot be null.")
    @NotBlank(message = "Passcode cannot be blank.")
    private String authType; // PIN|PASSCODE|BIOMETRIC
    
}
