package com.chh.trustfort.accounting.payload;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Data
public class AuthRequest {

    @NotBlank(message = "Username is required")
    private String userName;

    @NotBlank(message = "Password is required")
    private String password;
}
