package com.development.spring.hGate.H_Gate.security.services;

import com.development.spring.hGate.H_Gate.audit.AuditContext;
import com.development.spring.hGate.H_Gate.entity.Prenotazione;
import com.development.spring.hGate.H_Gate.repositories.AllegatoRepository;
import com.development.spring.hGate.H_Gate.repositories.PazienteRepository;
import com.development.spring.hGate.H_Gate.repositories.PazienteTutoreRepository;
import com.development.spring.hGate.H_Gate.repositories.PercorsiTerapeuticiRepository;
import com.development.spring.hGate.H_Gate.repositories.PrenotazioneRepository;
import com.development.spring.hGate.H_Gate.repositories.RefertoRepository;
import com.development.spring.hGate.H_Gate.repositories.ValutazionePsicologicaRepository;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.shared.models.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Controlli di accesso sui singoli dati clinici (non solo sul ruolo).
 * <p>
 * Usato nelle annotazioni {@code @PreAuthorize}, ad esempio:
 * {@code @PreAuthorize("@access.paziente(authentication, #id)")}.
 * <p>
 * Regole:
 * <ul>
 *   <li><b>ADMIN</b>: accesso in lettura a tutto.</li>
 *   <li><b>TUTORE</b>: solo i minori a cui è collegato in {@code pazienti_tutori}.</li>
 *   <li><b>MEDICO</b>: solo i pazienti che ha in carico, cioè con almeno una prenotazione
 *       con lui, un percorso o una valutazione creati da lui, oppure di cui è medico di riferimento.</li>
 * </ul>
 * Se il dato non esiste si restituisce {@code false} (403), per non rivelare quali ID esistono.
 */
@Component("access")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccessControlService {

    private final PazienteRepository pazienteRepository;
    private final PazienteTutoreRepository pazienteTutoreRepository;
    private final PrenotazioneRepository prenotazioneRepository;
    private final RefertoRepository refertoRepository;
    private final AllegatoRepository allegatoRepository;
    private final PercorsiTerapeuticiRepository percorsiRepository;
    private final ValutazionePsicologicaRepository valutazioneRepository;

    // ---------------------------------------------------------------- PAZIENTE

    public boolean paziente(Authentication authentication, Integer pazienteId) {
        AuditContext.risorsa("PAZIENTE", pazienteId);
        AuditContext.paziente(pazienteId);
        JwtAuthentication auth = asJwt(authentication);
        if (auth == null || pazienteId == null) return false;
        if (auth.hasRole(Role.ADMIN)) return true;
        if (auth.hasRole(Role.TUTORE) && isTutoreOf(auth.getId(), pazienteId)) return true;
        return auth.hasRole(Role.MEDICO) && isMedicoOf(auth.getId(), pazienteId);
    }

    // ------------------------------------------------------------ PRENOTAZIONE

    public boolean prenotazione(Authentication authentication, Integer prenotazioneId) {
        AuditContext.risorsa("PRENOTAZIONE", prenotazioneId);
        JwtAuthentication auth = asJwt(authentication);
        if (auth == null || prenotazioneId == null) return false;
        return prenotazioneRepository.findById(prenotazioneId)
                .map(p -> { AuditContext.paziente(p.getPaziente().getId()); return canSeePrenotazione(auth, p); })
                .orElse(false);
    }

    /** Solo il medico a cui è assegnata la prenotazione. */
    public boolean prenotazioneDelMedico(Authentication authentication, Integer prenotazioneId) {
        AuditContext.risorsa("PRENOTAZIONE", prenotazioneId);
        JwtAuthentication auth = asJwt(authentication);
        if (auth == null || prenotazioneId == null || !auth.hasRole(Role.MEDICO)) return false;
        return prenotazioneRepository.findById(prenotazioneId)
                .map(p -> { AuditContext.paziente(p.getPaziente().getId()); return isMedicoDellaPrenotazione(auth.getId(), p); })
                .orElse(false);
    }

    /** Solo un tutore collegato al minore della prenotazione. */
    public boolean prenotazioneDelTutore(Authentication authentication, Integer prenotazioneId) {
        AuditContext.risorsa("PRENOTAZIONE", prenotazioneId);
        JwtAuthentication auth = asJwt(authentication);
        if (auth == null || prenotazioneId == null || !auth.hasRole(Role.TUTORE)) return false;
        return prenotazioneRepository.findById(prenotazioneId)
                .map(p -> { AuditContext.paziente(p.getPaziente().getId()); return isTutoreOf(auth.getId(), p.getPaziente().getId()); })
                .orElse(false);
    }

    // ----------------------------------------------------------------- REFERTO

    public boolean referto(Authentication authentication, Integer refertoId) {
        AuditContext.risorsa("REFERTO", refertoId);
        JwtAuthentication auth = asJwt(authentication);
        if (auth == null || refertoId == null) return false;
        return refertoRepository.findById(refertoId)
                .map(r -> paziente(authentication, r.getPaziente().getId()))
                .orElse(false);
    }

    /** Solo il medico che ha emesso il referto (per modifica / eliminazione). */
    public boolean refertoDelMedico(Authentication authentication, Integer refertoId) {
        AuditContext.risorsa("REFERTO", refertoId);
        JwtAuthentication auth = asJwt(authentication);
        if (auth == null || refertoId == null || !auth.hasRole(Role.MEDICO)) return false;
        return refertoRepository.findById(refertoId)
                .map(r -> {
                    AuditContext.paziente(r.getPaziente().getId());
                    return r.getMedico() != null && auth.getId().equals(r.getMedico().getUser().getId());
                })
                .orElse(false);
    }

    // ---------------------------------------------------------------- ALLEGATO

    public boolean allegato(Authentication authentication, Integer allegatoId) {
        AuditContext.risorsa("ALLEGATO", allegatoId);
        JwtAuthentication auth = asJwt(authentication);
        if (auth == null || allegatoId == null) return false;
        return allegatoRepository.findById(allegatoId)
                .map(a -> { AuditContext.paziente(a.getPrenotazione().getPaziente().getId()); return canSeePrenotazione(auth, a.getPrenotazione()); })
                .orElse(false);
    }

    // ---------------------------------------------------------------- PERCORSO

    public boolean percorso(Authentication authentication, Integer percorsoId) {
        AuditContext.risorsa("PERCORSO", percorsoId);
        JwtAuthentication auth = asJwt(authentication);
        if (auth == null || percorsoId == null) return false;
        return percorsiRepository.findById(percorsoId)
                .map(p -> paziente(authentication, p.getPaziente().getId()))
                .orElse(false);
    }

    /** Un medico che ha in carico il paziente del percorso (es. per aggiornare le sedute). */
    public boolean percorsoModificabile(Authentication authentication, Integer percorsoId) {
        AuditContext.risorsa("PERCORSO", percorsoId);
        percorsiRepository.findById(percorsoId == null ? -1 : percorsoId).ifPresent(p -> AuditContext.paziente(p.getPaziente().getId()));
        JwtAuthentication auth = asJwt(authentication);
        if (auth == null || percorsoId == null || !auth.hasRole(Role.MEDICO)) return false;
        return percorsiRepository.findById(percorsoId)
                .map(p -> isMedicoOf(auth.getId(), p.getPaziente().getId()))
                .orElse(false);
    }

    /** Solo il medico autore del percorso. */
    public boolean percorsoDelMedico(Authentication authentication, Integer percorsoId) {
        AuditContext.risorsa("PERCORSO", percorsoId);
        percorsiRepository.findById(percorsoId == null ? -1 : percorsoId).ifPresent(p -> AuditContext.paziente(p.getPaziente().getId()));
        JwtAuthentication auth = asJwt(authentication);
        if (auth == null || percorsoId == null || !auth.hasRole(Role.MEDICO)) return false;
        return percorsiRepository.findById(percorsoId)
                .map(p -> auth.getId().equals(p.getMedico().getId()))
                .orElse(false);
    }

    // ------------------------------------------------------------- VALUTAZIONE

    public boolean valutazione(Authentication authentication, Integer valutazioneId) {
        AuditContext.risorsa("VALUTAZIONE", valutazioneId);
        JwtAuthentication auth = asJwt(authentication);
        if (auth == null || valutazioneId == null) return false;
        return valutazioneRepository.findById(valutazioneId)
                .map(v -> paziente(authentication, v.getPaziente().getId()))
                .orElse(false);
    }

    /** Solo il medico autore della valutazione. */
    public boolean valutazioneDelMedico(Authentication authentication, Integer valutazioneId) {
        AuditContext.risorsa("VALUTAZIONE", valutazioneId);
        valutazioneRepository.findById(valutazioneId == null ? -1 : valutazioneId).ifPresent(v -> AuditContext.paziente(v.getPaziente().getId()));
        JwtAuthentication auth = asJwt(authentication);
        if (auth == null || valutazioneId == null || !auth.hasRole(Role.MEDICO)) return false;
        return valutazioneRepository.findById(valutazioneId)
                .map(v -> auth.getId().equals(v.getMedico().getId()))
                .orElse(false);
    }

    // ----------------------------------------------------------------- HELPERS

    private boolean canSeePrenotazione(JwtAuthentication auth, Prenotazione p) {
        if (auth.hasRole(Role.ADMIN)) return true;
        if (auth.hasRole(Role.MEDICO) && isMedicoDellaPrenotazione(auth.getId(), p)) return true;
        return auth.hasRole(Role.TUTORE) && isTutoreOf(auth.getId(), p.getPaziente().getId());
    }

    private boolean isMedicoDellaPrenotazione(Integer userId, Prenotazione p) {
        return p.getMedico() != null && userId.equals(p.getMedico().getUser().getId());
    }

    private boolean isTutoreOf(Integer tutoreUserId, Integer pazienteId) {
        return pazienteTutoreRepository.existsByPazienteIdAndTutoreUserId(pazienteId, tutoreUserId);
    }

    private boolean isMedicoOf(Integer medicoUserId, Integer pazienteId) {
        return prenotazioneRepository.existsByPazienteIdAndMedicoUserId(pazienteId, medicoUserId)
                || percorsiRepository.existsByPazienteIdAndMedicoId(pazienteId, medicoUserId)
                || valutazioneRepository.existsByPazienteIdAndMedicoId(pazienteId, medicoUserId)
                || pazienteRepository.existsByIdAndMedicoUserId(pazienteId, medicoUserId);
    }

    private JwtAuthentication asJwt(Authentication authentication) {
        if (authentication instanceof JwtAuthentication jwt && jwt.getId() != null) {
            return jwt;
        }
        return null;
    }
}
