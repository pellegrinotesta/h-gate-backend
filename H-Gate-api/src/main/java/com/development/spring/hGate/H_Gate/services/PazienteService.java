package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.entity.Paziente;
import com.development.spring.hGate.H_Gate.repositories.PazienteRepository;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.shared.services.BasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PazienteService extends BasicService {

    private final PazienteRepository pazienteRepository;

    public Paziente findByUserId(JwtAuthentication jwtAuthentication) {
        return pazienteRepository.findByUserId(jwtAuthentication.getId());
    }
}
