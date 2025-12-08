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

    private static final String PAZIENTE_ID_NOT_FOUND = "Paziente with id %d not found.";

    public Paziente findByUserId(JwtAuthentication jwtAuthentication) {
        return pazienteRepository.findByUserId(jwtAuthentication.getId());
    }

    public Paziente updateInfo(Paziente pazienteUpdate) {
        Paziente existing = pazienteRepository.findById(pazienteUpdate.getId()).orElseThrow(() -> buildEntityWithIdNotFoundException(pazienteUpdate.getId(), PAZIENTE_ID_NOT_FOUND));
        existing.setAllergie(pazienteUpdate.getAllergie());
        existing.setCodiceFiscale(pazienteUpdate.getCodiceFiscale());
        existing.setAltezzaCm(pazienteUpdate.getAltezzaCm());
        existing.setGruppoSanguigno(pazienteUpdate.getGruppoSanguigno());
        existing.setNoteMediche(pazienteUpdate.getNoteMediche());
        existing.setPatologieCroniche(pazienteUpdate.getPatologieCroniche());
        existing.setPesoKg(pazienteUpdate.getPesoKg());
        return pazienteRepository.save(existing);
    }
}
