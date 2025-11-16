package com.development.spring.hGate.H_Gate.entity;

import com.development.spring.hGate.H_Gate.shared.entities.BasicEntity;
import com.development.spring.hGate.H_Gate.shared.models.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class Users extends BasicEntity {

    private String password;

    private String email;

    private String name;

    private String surname;

    private String status;

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
