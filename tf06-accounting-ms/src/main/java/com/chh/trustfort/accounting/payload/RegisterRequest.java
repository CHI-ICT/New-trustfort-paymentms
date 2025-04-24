package com.chh.trustfort.accounting.payload;

import com.chh.trustfort.accounting.component.UserClass;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 4, max = 20, message = "Username must be 4-20 characters")
    private String userName;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "PIN is required")
    private String pin;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String emailAddress;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotNull(message = "User class must not be null")
    private UserClass userClass;

    @NotBlank(message = "Device ID is required")
    private String deviceId;
}
