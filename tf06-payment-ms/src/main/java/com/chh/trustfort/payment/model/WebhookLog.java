package com.chh.trustfort.payment.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "webhook_log")
public class WebhookLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String txRef;
    private String eventType;
    @Lob
    private String rawPayload;
    private String sourceIp;

    private boolean processed;

//    @Column(name = "reference")
    private String reference;


    @CreationTimestamp
    private LocalDateTime receivedAt;
}
