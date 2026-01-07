package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.dtos.*;
import com.development.spring.hGate.H_Gate.entity.*;
import com.development.spring.hGate.H_Gate.enums.StatoPrenotazioneEnum;
import com.development.spring.hGate.H_Gate.repositories.MedicoRepository;
import com.development.spring.hGate.H_Gate.repositories.PrenotazioneRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrenotazioniDettagliateService extends BasicService {

    private final PrenotazioniDettagliateRepository prenotazioniDettagliateRepository;
    private final RefertoRepository refertoRepository;
    private final MedicoRepository medicoRepository;

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

    public List<PrenotazioneDTO> appuntamentiOggi(Integer medicoUserId){
        // Visite oggi
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        List<VPrenotazioniDettagliate>  prenotazioniDettagliate = prenotazioniDettagliateRepository.findByMedicoUserIdAndDataOraBetweenAndStatoInOrderByDataOraAsc(medicoUserId, startOfDay, endOfDay, List.of(StatoPrenotazioneEnum.CONFERMATA.name(), StatoPrenotazioneEnum.IN_ATTESA.name()));

        return prenotazioniDettagliate.stream().map(this::mapToPrenotazioneDTO).toList();
    }

    public List<MedicoDaVerificareDTO> mediciInAttesa() {
        return medicoRepository
                .findByIsVerificatoFalse()
                .stream()
                .map(this::mapToMedicoDaVerificareDTO)
                .collect(Collectors.toList());
    }


    private PrenotazioneDTO mapToPrenotazioneDTO(VPrenotazioniDettagliate prenotazione) {
        return PrenotazioneDTO.builder()
                .numeroPrenotazione(prenotazione.getNumeroPrenotazione())
                .dataOra(prenotazione.getDataOra())
                .dataOraFine(prenotazione.getDataOraFine())
                .tipoVisita(prenotazione.getTipoVisita())
                .stato(prenotazione.getStato())
                .costo(prenotazione.getCosto())
                .paziente(PazienteMinDTO.builder()
                        .nome(prenotazione.getPazienteNome())
                        .codiceFiscale(prenotazione.getPazienteCf())
                        .email(prenotazione.getPazienteEmail())
                        .build())
//                .medico(mapToMedicoMinDTO(prenotazione.getMedico()))
                .build();
    }

    private RefertoDTO mapToRefertoDTO(Referto referto) {
        return RefertoDTO.builder()
                .titolo(referto.getTitolo())
                .dataEmissione(referto.getDataEmissione())
                .tipoReferto(referto.getTipoReferto())
                .diagnosi(referto.getDiagnosi())
//                .hasAllegati(!referto.getAllegati().isEmpty())
//                .medico(mapToMedicoMinDTO(referto.getMedico()))
                .build();
    }

    private PazienteMinDTO mapToPazienteMinDTO(Paziente paziente) {
        return PazienteMinDTO.builder()
                .id(paziente.getId())
                .codiceFiscale(paziente.getCodiceFiscale())
                .build();
    }

    private MedicoMinDTO mapToMedicoMinDTO(Medico medico) {
        return MedicoMinDTO.builder()
                .id(medico.getId())
                .nome(medico.getUser().getNome())
                .cognome(medico.getUser().getCognome())
                .specializzazione(medico.getSpecializzazione())
                .email(medico.getUser().getEmail())
                .ratingMedio(medico.getRatingMedio())
                .build();
    }

    private MedicoDaVerificareDTO mapToMedicoDaVerificareDTO(Medico medico) {
        return MedicoDaVerificareDTO.builder()
                .id(medico.getId())
                .nome(medico.getUser().getNome())
                .cognome(medico.getUser().getCognome())
                .email(medico.getUser().getEmail())
                .specializzazione(medico.getSpecializzazione())
                .numeroAlbo(medico.getNumeroAlbo())
                .universita(medico.getUniversita())
                .annoLaurea(medico.getAnnoLaurea())
                .hasDocuments(true)
                .build();
    }

}
