package com.chh.trustfort.accounting.dto.investment;

import lombok.Data;

@Data
public class InstitutionRequestDTO {
    private String name;
    private String code;
    private String contactEmail;
    private String contactPhone;
    private String address;
    private String createdBy;
}
