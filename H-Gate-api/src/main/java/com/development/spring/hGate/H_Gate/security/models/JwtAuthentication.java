package com.development.spring.hGate.H_Gate.security.models;

import com.development.spring.hGate.H_Gate.shared.models.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JwtAuthentication implements Authentication {

    private Long id;
    @JsonProperty("sub")
    private String username;
    private Long iat; // Epoch in seconds
    private Long exp; // Epoch in seconds
    private Set<Role> authorities;
    private boolean isAuthenticated;
    private Object details;
    private Object credentials;
    public Date getExpirationDate() {
        return new Date(exp * 1000);
    }

    public Date getCreationDate() {
        return new Date(iat * 1000);
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

    @Override
    public String getName() {
        return username;
    }

    public boolean hasRole(Role role) {
        if (authorities == null) {
            return false;
        }
        return authorities.contains(role);
    }

    public boolean hasAnyRole(Collection<Role> roles) {
        for (Role role : roles) {
            if (hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getIat() {
        return iat;
    }

    public void setIat(Long iat) {
        this.iat = iat;
    }

    public Long getExp() {
        return exp;
    }

    public void setExp(Long exp) {
        this.exp = exp;
    }

    @Override
    public Set<Role> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<Role> authorities) {
        this.authorities = authorities;
    }

    @Override
    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

    @Override
    public Object getDetails() {
        return details;
    }

    public void setDetails(Object details) {
        this.details = details;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    public void setCredentials(Object credentials) {
        this.credentials = credentials;
    }
}
