package com.chh.trustfort.accounting.dto;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author dofoleta
 */
@Getter
@Setter
public class Data {
    private int id;
    private String bvn;
    private String firstname;
    private String middlename;
    private String lastname;
    private String birthdate;
    private String gender;
    private String photo;
    private String phone;
    private FieldMatches fieldMatches;
}
