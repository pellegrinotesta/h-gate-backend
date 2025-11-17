package com.development.spring.hGate.H_Gate.entity;

import com.development.spring.hGate.H_Gate.enums.TipoNotificaEnum;
import com.development.spring.hGate.H_Gate.shared.entities.BasicEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notifiche")
public class Notifica extends BasicEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNotificaEnum tipo;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String titolo;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String messaggio;

    @Column(length = 500)
    private String link;

    @Column(name = "is_letta")
    private Boolean isLetta = false;

    @Column(name = "data_lettura")
    private LocalDateTime dataLettura;

    @Column(name = "is_inviata_email")
    private Boolean isInviataEmail = false;

    @Column(name = "data_invio_email")
    private LocalDateTime dataInvioEmail;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
