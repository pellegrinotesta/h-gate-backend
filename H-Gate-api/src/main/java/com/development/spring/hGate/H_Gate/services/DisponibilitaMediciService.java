package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.entity.DisponibilitaMedico;
import com.development.spring.hGate.H_Gate.repositories.DisponibilitaMediciRepository;
import com.development.spring.hGate.H_Gate.shared.services.BasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DisponibilitaMediciService extends BasicService {

    private final DisponibilitaMediciRepository disponibilitaMediciRepository;

    public List<DisponibilitaMedico> findByMedicoUserId(Integer medicoUserId) {
        return disponibilitaMediciRepository.findByMedicoUserId(medicoUserId);
    }
}
