package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.repositories.MedicoRepository;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.shared.services.BasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MedicoService extends BasicService {

    private final MedicoRepository medicoRepository;

    public Medico findMedicoByUserId(JwtAuthentication jwtAuthentication) {
        return medicoRepository.findMedicoByUserId(jwtAuthentication.getId());
    }
}
