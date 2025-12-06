package com.development.spring.hGate.H_Gate.entity;

import com.development.spring.hGate.H_Gate.shared.entities.BasicEntity;
import com.development.spring.hGate.H_Gate.shared.models.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class Users extends BasicEntity {

    @Column(unique = true, nullable = false, length = 36)
    private String uuid;

    @Email
    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    @NotBlank
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(nullable = false, length = 100)
    private String nome;

    @NotBlank
    @Size(min = 2, max = 100)
    @Column(nullable = false, length = 100)
    private String cognome;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$")
    @Column(length = 20)
    private String telefono;

    @Column(name = "data_nascita")
    private Date dataNascita;

    @Column(columnDefinition = "TEXT")
    private String indirizzo;

    @Column(length = 100)
    private String citta;

    @Column(length = 2)
    private String provincia;

    @Column(length = 5)
    private String cap;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "reset_password_expires")
    private LocalDateTime resetPasswordExpires;

    @Column(name = "ultimo_accesso")
    private Date ultimoAccesso;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    // Metodi helper
    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
    }

    @JsonProperty("nomeCompleto")
    public String getNomeCompleto() {
        return nome + " " + cognome;
    }

    public @NotEmpty Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(@NotEmpty Set<Role> roles) {
        this.roles = roles;
    }

    @NotEmpty
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @JoinTable(name = "roles", joinColumns = @JoinColumn(name = "user_id"))
    private Set<Role> roles;
}
