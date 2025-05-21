package com.chh.trustfort.accounting.payload;

import com.chh.trustfort.accounting.enums.Subsidiary;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class EntityCodeRequest {
    private Subsidiary subsidiary;
    private String code;
}
