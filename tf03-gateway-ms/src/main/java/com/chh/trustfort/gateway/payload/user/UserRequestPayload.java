package com.chh.trustfort.gateway.payload.user;

import java.time.LocalDate;
import javax.validation.constraints.*;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 *
 * @author dofoleta
 */
@Data
public class UserRequestPayload {

    @NotBlank(message = "User name cannot be blanck.")
    @NotNull(message = "User name cannot be null.")
    @Size(min = 11, max = 11, message = "User name must be exactly 11 characters.")
    private String userName;

    @NotNull(message = "Passcode cannot be null.")
    @NotBlank(message = "Passcode cannot be blank.")
    @Size(min = 6, max = 6, message = "Passcode must be exactly 6 characters.")
    @Pattern(regexp = "^[0-9]{6}$", message = "Passcode must contain only numeric characters.")
    private String passcode;
    
    @NotNull(message = "Email address cannot be null.")
    @NotBlank(message = "Email address cannot be blank.")
    @Email(message = "Email address should be valid.")
    private String emailAddress;
    
    @NotNull(message = "First name cannot be null.")
    @NotBlank(message = "First name cannot be blank.")
    @Size(max = 50, message = "First name must be less than 50 characters.")
    private String firstName;

    @NotNull(message = "Last name cannot be null.")
    @NotBlank(message = "Last name cannot be blank.")
    @Size(max = 50, message = "Last name must be less than 50 characters.")
    private String lastName;

    @Size(max = 50, message = "Middle name must be less than 50 characters.")
    private String middleName; // Optional field (no @NotNull or @NotBlank)

    @NotNull(message = "Date of birth cannot be null.")
    @Past(message = "Date of birth must be in the past.")
    private LocalDate dob;
    
    @NotNull(message = "User type cannot be null.")
    @NotBlank(message = "User type cannot be blank.")
    @Size(min = 1,max = 1, message = "User type must be exactly 1 character (I or C).")
    private String userType;
    
    @NotNull(message = "User class cannot be null.")
    @NotBlank(message = "User class cannot be blank.")
    @Size(max = 50, message = "User class must be ess than 50 characters.")
    private String userClass;


}
