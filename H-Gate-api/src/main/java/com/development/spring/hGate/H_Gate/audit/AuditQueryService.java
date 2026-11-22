package com.development.spring.hGate.H_Gate.audit;

import com.development.spring.hGate.H_Gate.entity.Paziente;
import com.development.spring.hGate.H_Gate.entity.Users;
import com.development.spring.hGate.H_Gate.repositories.PazienteRepository;
import com.development.spring.hGate.H_Gate.repositories.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditQueryService {

    private static final int MAX_PAGE_SIZE = 200;

    private final AuditLogRepository repository;
    private final UserRepository userRepository;
    private final PazienteRepository pazienteRepository;

    public record Filtri(String email, String codiceFiscale, Integer pazienteId,
                         AuditLog.AzioneAudit azione, AuditLog.EsitoAudit esito,
                         LocalDate da, LocalDate a) {}

    public Page<AuditLogDTO> cerca(Filtri f, int page, int size) {
        Integer pazienteId = f.pazienteId();
        if (f.codiceFiscale() != null && !f.codiceFiscale().isBlank()) {
            Optional<Paziente> p = pazienteRepository.findByCodiceFiscale(f.codiceFiscale().trim().toUpperCase());
            if (p.isEmpty()) return Page.empty(PageRequest.of(page, 1));
            pazienteId = p.get().getId();
        }
        Integer pid = pazienteId;

        Specification<AuditLog> spec = (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            if (f.email() != null && !f.email().isBlank())
                ps.add(cb.like(cb.lower(root.get("userEmail")), "%" + f.email().trim().toLowerCase() + "%"));
            if (pid != null) ps.add(cb.equal(root.get("pazienteId"), pid));
            if (f.azione() != null) ps.add(cb.equal(root.get("azione"), f.azione()));
            if (f.esito() != null) ps.add(cb.equal(root.get("esito"), f.esito()));
            if (f.da() != null) ps.add(cb.greaterThanOrEqualTo(root.get("dataEvento"), f.da().atStartOfDay()));
            if (f.a() != null) ps.add(cb.lessThan(root.get("dataEvento"), f.a().plusDays(1).atStartOfDay()));
            return cb.and(ps.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), MAX_PAGE_SIZE),
                Sort.by(Sort.Direction.DESC, "dataEvento", "id"));
        Page<AuditLog> righe = repository.findAll(spec, pageable);

        Map<Integer, Users> utenti = utenti(righe.getContent());
        Map<Integer, Paziente> pazienti = pazienteRepository.findAllById(righe.getContent().stream()
                        .map(AuditLog::getPazienteId).filter(Objects::nonNull).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(Paziente::getId, Function.identity()));

        return righe.map(r -> AuditLogDTO.builder()
                .id(r.getId())
                .dataEvento(r.getDataEvento())
                .userId(r.getUserId())
                .userEmail(r.getUserEmail())
                .utenteNome(Optional.ofNullable(utenti.get(r.getUserId())).map(u -> u.getNome() + " " + u.getCognome()).orElse(null))
                .ruoli(r.getRuoli())
                .azione(r.getAzione())
                .esito(r.getEsito())
                .risorsaTipo(r.getRisorsaTipo())
                .risorsaId(r.getRisorsaId())
                .pazienteId(r.getPazienteId())
                .pazienteNome(Optional.ofNullable(pazienti.get(r.getPazienteId())).map(p -> p.getNome() + " " + p.getCognome()).orElse(null))
                .httpStatus(r.getHttpStatus())
                .httpMethod(r.getHttpMethod())
                .path(r.getPath())
                .ipAddress(r.getIpAddress())
                .build());
    }

    /**
     * Accessi di altri utenti ai dati di un minore (ultimi {@code limite}), per il tutore.
     * Sono esclusi gli accessi del tutore stesso.
     */
    public List<AccessoPazienteDTO> accessiPaziente(Integer pazienteId, Integer escludiUserId, int limite) {
        Specification<AuditLog> spec = (root, query, cb) -> cb.and(
                cb.equal(root.get("pazienteId"), pazienteId),
                escludiUserId == null ? cb.conjunction() : cb.or(cb.isNull(root.get("userId")), cb.notEqual(root.get("userId"), escludiUserId)));
        List<AuditLog> righe = repository.findAll(spec,
                PageRequest.of(0, Math.min(limite, MAX_PAGE_SIZE), Sort.by(Sort.Direction.DESC, "dataEvento", "id"))).getContent();
        Map<Integer, Users> utenti = utenti(righe);
        return righe.stream().map(r -> AccessoPazienteDTO.builder()
                .dataEvento(r.getDataEvento())
                .utente(Optional.ofNullable(utenti.get(r.getUserId())).map(u -> u.getNome() + " " + u.getCognome()).orElse(r.getUserEmail()))
                .ruolo(ruoloLeggibile(r.getRuoli()))
                .azione(r.getAzione())
                .risorsaTipo(r.getRisorsaTipo())
                .esito(r.getEsito())
                .build()).toList();
    }

    private Map<Integer, Users> utenti(List<AuditLog> righe) {
        Set<Integer> ids = righe.stream().map(AuditLog::getUserId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Integer, Users> map = new HashMap<>();
        userRepository.findAllById(ids).forEach(u -> map.put(u.getId(), u));
        return map;
    }

    private static String ruoloLeggibile(String ruoli) {
        if (ruoli == null) return null;
        if (ruoli.contains("MEDICO")) return "Specialista";
        if (ruoli.contains("ADMIN")) return "Amministrazione";
        if (ruoli.contains("TUTORE")) return "Tutore";
        return ruoli;
    }
}
