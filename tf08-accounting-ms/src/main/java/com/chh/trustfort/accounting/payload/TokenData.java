package com.chh.trustfort.accounting.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import lombok.Data;

/**
 *
 * @author dofoleta
 */
@Data
public class TokenData {

    private String sub;
    private ArrayList<String> roles;
    private String auth;
    @JsonProperty("Channel")
    private String channel;
    @JsonProperty("IP")
    private String iP;
    private String iss;
    private int iat;
    private int exp;
}
