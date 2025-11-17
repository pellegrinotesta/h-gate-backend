package com.development.spring.hGate.H_Gate.security.configs;

import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.security.services.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@RequiredArgsConstructor
public class ScsAuditorAwareImpl implements AuditorAware<Integer> {

    @Autowired
    private SessionService sessionService;

    @Override
    public Optional<Integer> getCurrentAuditor() {
        JwtAuthentication jwtAuthentication = sessionService.getCurrentUser();
        return Optional.ofNullable(jwtAuthentication != null ? jwtAuthentication.getId() : null);
    }

}
