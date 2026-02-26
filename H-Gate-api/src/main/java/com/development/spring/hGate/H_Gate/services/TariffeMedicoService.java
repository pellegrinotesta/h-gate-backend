package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.dtos.medici.TariffeMediciDTO;
import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.entity.TariffeMedici;
import com.development.spring.hGate.H_Gate.repositories.MedicoRepository;
import com.development.spring.hGate.H_Gate.repositories.TariffeMediciRepository;
import com.development.spring.hGate.H_Gate.shared.services.BasicService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TariffeMedicoService extends BasicService {

    private final TariffeMediciRepository tariffeMediciRepository;
    private final MedicoRepository medicoRepository;

    public List<TariffeMedici> getByMedico(Integer userId) {
        Medico medico = medicoRepository.findMedicoByUserId(userId);
        return tariffeMediciRepository.findByMedicoId(medico.getId());
    }

    @Transactional
    public TariffeMedici create(Integer medicoUserId, TariffeMediciDTO dto) {
        Medico medico = medicoRepository.findMedicoByUserId(medicoUserId);
        TariffeMedici tariffeMedici = TariffeMedici.builder()
                .costo(dto.getCosto())
                .medico(medico)
                .durataMinuti(dto.getDurataMinuti())
                .isAttiva(dto.isAttiva())
                .isPrimaVisita(dto.isPrimaVisita())
                .tipoVisita(dto.getTipoVisita())
                .build();

        return tariffeMediciRepository.save(tariffeMedici);
    }

    @Transactional
    public TariffeMedici update(Integer id, Integer medicoUserId, TariffeMediciDTO dto) {
        Medico medico = medicoRepository.findMedicoByUserId(medicoUserId);
        TariffeMedici tariffeMedici = findAndVerifyOwnership(id, medico);

        tariffeMedici.setCosto(dto.getCosto());
        tariffeMedici.setAttiva(dto.isAttiva());
        tariffeMedici.setDurataMinuti(dto.getDurataMinuti());
        tariffeMedici.setTipoVisita(dto.getTipoVisita());
        tariffeMedici.setPrimaVisita(dto.isPrimaVisita());

        return tariffeMediciRepository.save(tariffeMedici);

    }

    @Transactional
    public void delete(Integer medicoUserId, Integer id) {
        Medico medico = medicoRepository.findMedicoByUserId(medicoUserId);
        TariffeMedici tariffeMedici = findAndVerifyOwnership(id, medico);
        tariffeMediciRepository.delete(tariffeMedici);
    }

    @Transactional
    public TariffeMedici toggleAttiva(Integer medicoUserId, Integer id) {
        Medico medico = medicoRepository.findMedicoByUserId(medicoUserId);
        TariffeMedici tariffeMedici = findAndVerifyOwnership(id, medico);
        tariffeMedici.setAttiva(!tariffeMedici.isAttiva());
        return tariffeMediciRepository.save(tariffeMedici);
    }

    private TariffeMedici findAndVerifyOwnership(Integer id, Medico medico) {
        TariffeMedici tariffa = tariffeMediciRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tariffa non trovata"));
        if(!tariffa.getMedico().getId().equals(medico.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non sei autorizzato");
        }
        return tariffa;
    }
}
