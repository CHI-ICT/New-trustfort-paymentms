package com.chh.trustfort.accounting.dto;

import com.chh.trustfort.accounting.model.Users;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class ValidatedRequest {
    private boolean isError;
    private String payload;
    private String rawJson;
    private Users user;
    private String idToken;
    // getters/setters
}
