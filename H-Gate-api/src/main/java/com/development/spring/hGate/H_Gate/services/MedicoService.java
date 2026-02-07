package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.entity.TariffeMedici;
import com.development.spring.hGate.H_Gate.repositories.MedicoRepository;
import com.development.spring.hGate.H_Gate.repositories.TariffeMediciRepository;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.shared.services.BasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicoService extends BasicService {

    private final MedicoRepository medicoRepository;
    private final TariffeMediciRepository tariffeMediciRepository;

    private static final String MEDICO_ID_NOT_FOUND = "Medico with id %d not found.";

    public Medico findMedicoByUserId(JwtAuthentication jwtAuthentication) {
        return medicoRepository.findMedicoByUserId(jwtAuthentication.getId());
    }

    public List<Medico> getAll() {
        return medicoRepository.findAll();
    }

    public Medico update(Medico medico) {
        Medico medicoExisting = medicoRepository.findById(medico.getId()).orElseThrow(() -> buildEntityWithIdNotFoundException(medico.getId(), MEDICO_ID_NOT_FOUND));

        medicoExisting.setAnnoLaurea(medico.getAnnoLaurea());
        medicoExisting.setBio(medico.getBio());
        medicoExisting.setCurriculum(medico.getCurriculum());
        medicoExisting.setDurataVisitaMinuti(medico.getDurataVisitaMinuti());
        medicoExisting.setNumeroAlbo(medico.getNumeroAlbo());
        medicoExisting.setUniversita(medico.getUniversita());

        return medicoRepository.save(medicoExisting);
    }

    public List<TariffeMedici> listaTariffeMedico(Integer medicoId) {
        return tariffeMediciRepository.findByMedicoId(medicoId);
    }
}
