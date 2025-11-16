package com.development.spring.hGate.H_Gate.entity;

import com.development.spring.hGate.H_Gate.shared.entities.BasicEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "password_reset_token")
public class PasswordResetToken extends BasicEntity {

    private static final int EXPIRATION = 30; //30 minuti

    @Id
    private Long id;

    private String token;

    @OneToOne(targetEntity = Users.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private Users user;

    @Column(name = "expiry_date")
    private Date expiryDate;

    public PasswordResetToken(String token, Users user) {
        this.token = token;
        this.user = user;
        this.expiryDate = calculateExpiryDate();
    }

    private Date calculateExpiryDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, EXPIRATION);
        return cal.getTime();
    }

    @Override
    public String toString() {
        return "PasswordResetToken{" +
                "token='" + token + '\'' +
                ", userId=" + (user != null ? user.getId() : null) +
                ", expiryDate=" + expiryDate +
                '}';
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
