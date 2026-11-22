package com.development.spring.hGate.H_Gate.audit;

import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Scrive le righe del registro accessi. Un errore di scrittura viene registrato nei log
 * applicativi ma non blocca l'operazione dell'utente.
 */
@Service
@RequiredArgsConstructor
public class AuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

    private final AuditLogRepository repository;

    public void registra(AuditLog.AzioneAudit azione, AuditLog.EsitoAudit esito,
                         JwtAuthentication auth, String email,
                         String risorsaTipo, Integer risorsaId, Integer pazienteId,
                         Integer httpStatus, HttpServletRequest request) {
        try {
            repository.save(AuditLog.builder()
                    .dataEvento(LocalDateTime.now())
                    .userId(auth != null ? auth.getId() : null)
                    .userEmail(auth != null ? auth.getUsername() : troncato(email, 255))
                    .ruoli(auth != null && auth.getAuthorities() != null
                            ? auth.getAuthorities().stream().map(Enum::name).sorted().collect(Collectors.joining(","))
                            : null)
                    .azione(azione)
                    .esito(esito)
                    .risorsaTipo(risorsaTipo)
                    .risorsaId(risorsaId)
                    .pazienteId(pazienteId)
                    .httpStatus(httpStatus)
                    .httpMethod(request != null ? request.getMethod() : null)
                    .path(request != null ? troncato(request.getRequestURI(), 500) : null)
                    .ipAddress(request != null ? clientIp(request) : null)
                    .userAgent(request != null ? troncato(request.getHeader("User-Agent"), 300) : null)
                    .build());
        } catch (Exception e) {
            logger.error("Impossibile scrivere nel registro accessi: {} {} {}", azione, risorsaTipo, risorsaId, e);
        }
    }

    /**
     * IP del client. Dietro un proxy (nginx, Render) si usa l'ULTIMO valore di X-Forwarded-For,
     * aggiunto dal proxy stesso: i valori precedenti arrivano dal client e possono essere falsificati.
     * Se il backend fosse esposto direttamente senza proxy, anche questo header sarebbe falsificabile.
     */
    public static String clientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        String ip = request.getRemoteAddr();
        if (xff != null && !xff.isBlank()) {
            String[] parti = xff.split(",");
            ip = parti[parti.length - 1].trim();
        }
        return troncato(ip, 45);
    }

    private static String troncato(String s, int max) {
        if (s == null) return null;
        return s.length() > max ? s.substring(0, max) : s;
    }
}
