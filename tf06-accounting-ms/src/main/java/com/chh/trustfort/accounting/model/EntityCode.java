package com.chh.trustfort.accounting.model;

import com.chh.trustfort.accounting.enums.Subsidiary;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "entity_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntityCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private Subsidiary subsidiary;

    @Column(nullable = false, unique = true)
    private String code;
}
