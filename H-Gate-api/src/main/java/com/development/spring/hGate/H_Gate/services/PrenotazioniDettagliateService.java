package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.dtos.PrenotazioneDTO;
import com.development.spring.hGate.H_Gate.entity.VPrenotazioniDettagliate;
import com.development.spring.hGate.H_Gate.enums.StatoPrenotazioneEnum;
import com.development.spring.hGate.H_Gate.repositories.PrenotazioniDettagliateRepository;
import com.development.spring.hGate.H_Gate.repositories.RefertoRepository;
import com.development.spring.hGate.H_Gate.shared.services.BasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrenotazioniDettagliateService extends BasicService {

    private final PrenotazioniDettagliateRepository prenotazioniDettagliateRepository;
    private final RefertoRepository refertoRepository;

    public List<VPrenotazioniDettagliate> prenotazioniPaziente(Integer pazienteId) {
        return prenotazioniDettagliateRepository.findTop5ByPazienteUserIdAndDataOraAfterAndStatoInOrderByDataOraAsc(pazienteId, new Date(), List.of(StatoPrenotazioneEnum.CONFERMATA.name(), StatoPrenotazioneEnum.IN_ATTESA.name()));    }

    public Integer prossimiAppuntamenti(Integer pazienteUserId) {
        return prenotazioniDettagliateRepository.countByPazienteUserIdAndDataOraAfterAndStatoIn(pazienteUserId, new Date(), List.of(StatoPrenotazioneEnum.CONFERMATA.name(), StatoPrenotazioneEnum.IN_ATTESA.name()));
    }

    public Integer visiteTotali(Integer pazienteUserId) {
        return prenotazioniDettagliateRepository.countByPazienteUserId(pazienteUserId);
    }

    public Integer visiteOggi(Integer medicoUserId) {
        // Visite oggi
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return prenotazioniDettagliateRepository.countByMedicoUserIdAndDataOraBetweenAndStatoIn(medicoUserId, startOfDay, endOfDay, List.of(StatoPrenotazioneEnum.CONFERMATA.name(), StatoPrenotazioneEnum.IN_ATTESA.name()));
    }

    public Integer pazientiTotali(Integer medicoUserId) {
        return prenotazioniDettagliateRepository.countDistinctPazientiByMedico(medicoUserId);
    }

    public Integer refertiDaFirmare(Integer medicoUserId) {
        return refertoRepository.countByMedicoAndIsFirmato(medicoUserId);
    }

    public Integer refertiDaCompletare(Integer medicoUserId) {
        return prenotazioniDettagliateRepository.countByMedicoUserIdAndStato(medicoUserId, StatoPrenotazioneEnum.COMPLETATA.name());
    }

//    public List<PrenotazioneDTO> appuntamentiOggi(Integer medicoUserId){
//        return prenotazioniDettagliateRepository.
//    }

}
