package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.dtos.medici.DisponibilitaMediciDTO;
import com.development.spring.hGate.H_Gate.entity.DisponibilitaMedico;
import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.repositories.DisponibilitaMediciRepository;
import com.development.spring.hGate.H_Gate.repositories.MedicoRepository;
import com.development.spring.hGate.H_Gate.shared.services.BasicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisponibilitaMediciService extends BasicService {

    private final DisponibilitaMediciRepository disponibilitaMediciRepository;
    private final MedicoRepository medicoRepository;

    @Transactional
    public DisponibilitaMedico salvaDisponibilita(Integer medicoUserId, DisponibilitaMediciDTO dto) {
        Medico medico = medicoRepository.findMedicoByUserId(medicoUserId);

        if (dto.getOraInizio().isAfter(dto.getOraFine())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "L'ora di inizio deve essere prima dell'ora di fine"
            );
        }

        if (dto.getGiornoSettimana() < 0 || dto.getGiornoSettimana() > 6) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Giorno settimana non valido (0=Lunedì, 6=Domenica)"
            );
        }

        Optional<DisponibilitaMedico> esistente = disponibilitaMediciRepository
                .findByMedicoIdAndGiornoSettimana(medico.getId(), dto.getGiornoSettimana());

        DisponibilitaMedico disponibilita;

        if (esistente.isPresent()) {
            // Aggiorna esistente
            disponibilita = esistente.get();
            disponibilita.setOraInizio(dto.getOraInizio());
            disponibilita.setOraFine(dto.getOraFine());
            disponibilita.setIsAttiva(dto.getIsAttiva());
            disponibilita.setNote(dto.getNote());
        } else {
            // Crea nuovo
            disponibilita = DisponibilitaMedico.builder()
                    .medico(medico)
                    .giornoSettimana(dto.getGiornoSettimana())
                    .oraInizio(dto.getOraInizio())
                    .oraFine(dto.getOraFine())
                    .isAttiva(dto.getIsAttiva() != null ? dto.getIsAttiva() : true)
                    .note(dto.getNote())
                    .build();
        }

        return disponibilitaMediciRepository.save(disponibilita);
    }

    public List<DisponibilitaMedico> findByMedicoUserId(Integer medicoUserId) {
        return disponibilitaMediciRepository.findByMedicoUserId(medicoUserId);
    }

    /**
     * Ottiene tutte le disponibilità di un medico
     */
    @Transactional(readOnly = true)
    public List<DisponibilitaMedico> getDisponibilitaMedico(Integer medicoId) {
        return disponibilitaMediciRepository.findByMedicoIdOrderByGiornoSettimana(medicoId);
    }

    /**
     * Ottiene la disponibilità per uno specifico giorno della settimana
     */
    @Transactional(readOnly = true)
    public Optional<DisponibilitaMedico> getDisponibilitaPerGiorno(
            Integer medicoId,
            Integer giornoSettimana
    ) {
        return disponibilitaMediciRepository.findByMedicoIdAndGiornoSettimana(
                medicoId,
                giornoSettimana
        );
    }

    /**
     * Verifica se il medico è disponibile in una data specifica
     */
    @Transactional(readOnly = true)
    public boolean isMedicoDisponibile(Integer medicoId, LocalDate data, LocalTime ora) {
        // Converti LocalDate in giorno della settimana (0=Lunedì, 6=Domenica)
        int giornoSettimana = data.getDayOfWeek().getValue() - 1;

        Optional<DisponibilitaMedico> disponibilita = disponibilitaMediciRepository
                .findByMedicoIdAndGiornoSettimanaAndIsAttivaTrue(medicoId, giornoSettimana);

        if (disponibilita.isEmpty()) {
            return false;
        }

        DisponibilitaMedico disp = disponibilita.get();

        // Verifica se l'ora rientra nell'intervallo
        return !ora.isBefore(disp.getOraInizio()) &&
                !ora.isAfter(disp.getOraFine());
    }

    /**
     * Ottiene orari di apertura/chiusura per una data specifica
     */
    @Transactional(readOnly = true)
    public Optional<DisponibilitaMedico> getOrariGiorno(Integer medicoId, LocalDate data) {
        int giornoSettimana = data.getDayOfWeek().getValue() - 1;
        return disponibilitaMediciRepository.findByMedicoIdAndGiornoSettimanaAndIsAttivaTrue(
                medicoId,
                giornoSettimana
        );
    }

    /**
     * Disabilita la disponibilità per un giorno
     */
    @Transactional
    public String disabilitaGiorno(Integer medicoUserId, Integer giornoSettimana) {
        Medico medico = medicoRepository.findMedicoByUserId(medicoUserId);
        String messaggio;

        Optional<DisponibilitaMedico> disponibilita = disponibilitaMediciRepository
                .findByMedicoIdAndGiornoSettimana(medico.getId(), giornoSettimana);

        if (disponibilita.isPresent()) {
            DisponibilitaMedico disp = disponibilita.get();
            disp.setIsAttiva(false);
            disponibilitaMediciRepository.save(disp);
            messaggio = "Giorno disabilitato con successo";
        } else {
            messaggio = "Disponibilità non trovata";
        }

        return messaggio;
    }

    /**
     * Riabilita la disponibilità per un giorno
     */
    @Transactional
    public String abilitaGiorno(Integer medicoUserId, Integer giornoSettimana) {
        Medico medico = medicoRepository.findMedicoByUserId(medicoUserId);
        String messaggio;
        Optional<DisponibilitaMedico> disponibilita = disponibilitaMediciRepository
                .findByMedicoIdAndGiornoSettimana(medico.getId(), giornoSettimana);

        if (disponibilita.isPresent()) {
            DisponibilitaMedico disp = disponibilita.get();
            disp.setIsAttiva(true);
            disponibilitaMediciRepository.save(disp);
            messaggio = "Giorno abilitato con successo";
        } else {
            messaggio = "Disponibilità non configurata per questo giorno";
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Disponibilità non configurata per questo giorno"
            );
        }

        return messaggio;
    }

    /**
     * Elimina la disponibilità per un giorno
     */
    @Transactional
    public String eliminaDisponibilita(Integer medicoUserId, Integer giornoSettimana) {
        Medico medico = medicoRepository.findMedicoByUserId(medicoUserId);
        String messaggio;

        Optional<DisponibilitaMedico> disponibilita = disponibilitaMediciRepository
                .findByMedicoIdAndGiornoSettimana(medico.getId(), giornoSettimana);

        disponibilita.ifPresent(disponibilitaMediciRepository::delete);
        messaggio = "Eliminato con successo";

        return messaggio;
    }

    /**
     * Configura disponibilità standard (Lun-Ven 9-17)
     */
    @Transactional
    public List<DisponibilitaMedico> configuraDisponibilitaStandard(Integer medicoUserId) {
        Medico medico = medicoRepository.findMedicoByUserId(medicoUserId);

        LocalTime inizio = LocalTime.of(9, 0);
        LocalTime fine = LocalTime.of(17, 0);

        // Da Lunedì (0) a Venerdì (4)
        return List.of(0, 1, 2, 3, 4).stream()
                .map(giorno -> {
                    Optional<DisponibilitaMedico> esistente = disponibilitaMediciRepository
                            .findByMedicoIdAndGiornoSettimana(medico.getId(), giorno);

                    if (esistente.isPresent()) {
                        DisponibilitaMedico disp = esistente.get();
                        disp.setOraInizio(inizio);
                        disp.setOraFine(fine);
                        disp.setIsAttiva(true);
                        return disponibilitaMediciRepository.save(disp);
                    } else {
                        return disponibilitaMediciRepository.save(
                                DisponibilitaMedico.builder()
                                        .medico(medico)
                                        .giornoSettimana(giorno)
                                        .oraInizio(inizio)
                                        .oraFine(fine)
                                        .isAttiva(true)
                                        .build()
                        );
                    }
                })
                .collect(Collectors.toList());
    }
}
