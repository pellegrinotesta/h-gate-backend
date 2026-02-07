package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.dtos.medici.MedicoDaVerificareDTO;
import com.development.spring.hGate.H_Gate.dtos.prenotazioni.PrenotazioneDTO;
import com.development.spring.hGate.H_Gate.entity.*;
import com.development.spring.hGate.H_Gate.enums.StatoPrenotazioneEnum;
import com.development.spring.hGate.H_Gate.repositories.MedicoRepository;
import com.development.spring.hGate.H_Gate.repositories.PrenotazioniDettagliateRepository;
import com.development.spring.hGate.H_Gate.repositories.RefertoRepository;
import com.development.spring.hGate.H_Gate.shared.services.BasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrenotazioniDettagliateService extends BasicService {

    private final PrenotazioniDettagliateRepository prenotazioniDettagliateRepository;
    private final RefertoRepository refertoRepository;
    private final MedicoRepository medicoRepository;

    /**
     * Ottiene le prossime 5 prenotazioni per il tutore
     */
    public List<VPrenotazioniDettagliate> prenotazioniTutore(Integer tutoreUserId) {
        LocalDateTime now = LocalDateTime.now();
        List<String> stati = List.of(
                StatoPrenotazioneEnum.CONFERMATA.name(),
                StatoPrenotazioneEnum.IN_ATTESA.name()
        );

        return prenotazioniDettagliateRepository
                .findTop5ByTutoreUserIdAndDataOraAfterAndStatoInOrderByDataOraAsc(
                        tutoreUserId,
                        now,
                        stati
                );
    }

    /**
     * Conta i prossimi appuntamenti del tutore
     */
    public Integer prossimiAppuntamentiTutore(Integer tutoreUserId) {
        LocalDateTime now = LocalDateTime.now();
        List<String> stati = List.of(
                StatoPrenotazioneEnum.CONFERMATA.name(),
                StatoPrenotazioneEnum.IN_ATTESA.name()
        );

        return prenotazioniDettagliateRepository
                .countByTutoreUserIdAndDataOraAfterAndStatoIn(
                        tutoreUserId,
                        now,
                        stati
                );
    }

    /**
     * Conta tutte le visite del tutore
     */
    public Integer visiteTotaliTutore(Integer tutoreUserId) {
        return prenotazioniDettagliateRepository.countByTutoreUserId(tutoreUserId);
    }

    /**
     * Conta le visite di oggi del medico
     */
    public Integer visiteOggi(Integer medicoUserId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        List<String> stati = List.of(
                StatoPrenotazioneEnum.CONFERMATA.name(),
                StatoPrenotazioneEnum.IN_ATTESA.name()
        );

        return prenotazioniDettagliateRepository
                .countByMedicoUserIdAndDataOraBetweenAndStatoIn(
                        medicoUserId,
                        startOfDay,
                        endOfDay,
                        stati
                );
    }

    /**
     * Conta i pazienti totali del medico
     */
    public Integer pazientiTotali(Integer medicoUserId) {
        return prenotazioniDettagliateRepository
                .countDistinctPazientiByMedico(medicoUserId);
    }

    /**
     * Conta i referti da firmare del medico
     */
    public Integer refertiDaFirmare(Integer medicoUserId) {
        return refertoRepository.countByMedicoUserIdAndIsFirmatoFalse(medicoUserId);
    }

    /**
     * Conta i referti da completare (visite completate senza referto)
     */
    public Integer refertiDaCompletare(Integer medicoUserId) {
        return prenotazioniDettagliateRepository
                .countByMedicoUserIdAndStato(
                        medicoUserId,
                        StatoPrenotazioneEnum.COMPLETATA.name()
                );
    }

    /**
     * Ottiene gli appuntamenti di oggi del medico
     */
    public List<PrenotazioneDTO> appuntamentiOggi(Integer medicoUserId) {
//        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
//        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        List<String> stati = List.of(
                StatoPrenotazioneEnum.CONFERMATA.name(),
                StatoPrenotazioneEnum.IN_ATTESA.name()
        );

        List<VPrenotazioniDettagliate> prenotazioni = prenotazioniDettagliateRepository
                .findByMedicoUserIdAndStatoIn(
                        medicoUserId,
                        stati
                );

        return prenotazioni.stream()
                .map(this::mapToPrenotazioneDTO)
                .toList();
    }

    /**
     * Ottiene i medici in attesa di verifica
     */
    public List<MedicoDaVerificareDTO> mediciInAttesa() {
        return medicoRepository
                .findByIsVerificatoFalse()
                .stream()
                .map(this::mapToMedicoDaVerificareDTO)
                .collect(Collectors.toList());
    }

    private PrenotazioneDTO mapToPrenotazioneDTO(VPrenotazioniDettagliate v) {
        return PrenotazioneDTO.builder()
                .numeroPrenotazione(v.getNumeroPrenotazione())
                .dataOra(v.getDataOra())
                .dataOraFine(v.getDataOraFine())
                .tipoVisita(v.getTipoVisita())
                .stato(v.getStato())
                .costo(v.getCosto())
                .paziente(PrenotazioneDTO.PazienteMinDTO.builder()
                        .nome(v.getPazienteNome())
                        .cognome(v.getPazienteCognome())
                        .codiceFiscale(v.getPazienteCf())
                        .email(v.getTutoreEmail()) // Email del tutore
                        .build())
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