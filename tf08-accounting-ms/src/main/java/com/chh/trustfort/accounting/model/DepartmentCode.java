// ==== ENTITY: Department.java ====
package com.chh.trustfort.accounting.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@RequiredArgsConstructor
public class DepartmentCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 3)
    private String code;
    private boolean isDeleted = false;
    private String name;
}