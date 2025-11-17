package com.development.spring.hGate.H_Gate.shared.models;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {

    PAZIENTE,
    MEDICO,
    AMMINISTRATORE;


    public String getAuthority() {
        return this.name();
    }

}
