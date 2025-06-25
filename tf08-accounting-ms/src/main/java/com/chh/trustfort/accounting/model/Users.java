package com.chh.trustfort.accounting.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 *
 * @author DOfoleta
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class Users implements Serializable {

    @Id
    @Column(name = "user_id")
    private Long id;

    private String userName;

    private String email;

    private String ecred;

    private String role;

    @Column(name = "phone_number")
    public String phoneNumber;
}
