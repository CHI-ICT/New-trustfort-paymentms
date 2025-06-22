package com.chh.trustfort.payment.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.*;

import com.chh.trustfort.payment.component.UserClass;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
