package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.entity.VAgendaMedici;
import com.development.spring.hGate.H_Gate.repositories.AgendaMediciRepository;
import com.development.spring.hGate.H_Gate.repositories.MedicoRepository;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.shared.services.BasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendaMediciService extends BasicService {

    private final AgendaMediciRepository agendaMediciRepository;
    private final MedicoRepository medicoRepository;

    public List<VAgendaMedici> findByMedicoId(Integer userId) {
        Medico medico = medicoRepository.findMedicoByUserId(userId);
        List<VAgendaMedici> agenda;

        if(medico != null) {
            agenda = agendaMediciRepository.findByMedicoId(medico.getId());
        } else {
            throw new RuntimeException("Medico non trovato");
        }
        return agenda;
    }
}
