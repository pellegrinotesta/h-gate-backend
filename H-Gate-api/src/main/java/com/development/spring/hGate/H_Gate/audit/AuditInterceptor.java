package com.development.spring.hGate.H_Gate.audit;

import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
 * Registra ogni richiesta che riguarda dati clinici, a richiesta conclusa (quindi con l'esito reale:
 * OK, accesso NEGATO, ERRORE). Le richieste senza autenticazione valida si fermano prima, nel filtro di
 * sicurezza; i tentativi di login sono registrati da AuthenticationService.
 */
@Component
@RequiredArgsConstructor
public class AuditInterceptor implements HandlerInterceptor {

    /** Prefissi delle API con dati clinici o personali dei minori, e tipo di risorsa di default. */
    static final Map<String, String> PREFISSI = Map.ofEntries(
            Map.entry("/paziente", "PAZIENTE"),
            Map.entry("/referto", "REFERTO"),
            Map.entry("/prenotazione", "PRENOTAZIONE"),
            Map.entry("/allegati", "ALLEGATO"),
            Map.entry("/percorsi-terapeutici", "PERCORSO"),
            Map.entry("/valutazioni", "VALUTAZIONE"),
            Map.entry("/agenda", "AGENDA"),
            Map.entry("/consensi", "CONSENSO"),
            Map.entry("/admin/approvazioni", "APPROVAZIONE"),
            Map.entry("/admin/audit", "REGISTRO_ACCESSI")
    );

    private final AuditService auditService;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        String tipoDefault = PREFISSI.entrySet().stream()
                .filter(e -> path.equals(e.getKey()) || path.startsWith(e.getKey() + "/"))
                .map(Map.Entry::getValue)
                .findFirst().orElse(null);
        if (tipoDefault == null || "OPTIONS".equals(request.getMethod())) {
            return;
        }

        AuditContext ctx = AuditContext.current();
        int status = response.getStatus();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JwtAuthentication auth = authentication instanceof JwtAuthentication j ? j : null;

        auditService.registra(
                azione(request.getMethod(), path),
                status < 400 ? AuditLog.EsitoAudit.OK
                        : (status == 401 || status == 403) ? AuditLog.EsitoAudit.NEGATO
                        : AuditLog.EsitoAudit.ERRORE,
                auth, null,
                ctx != null && ctx.getRisorsaTipo() != null ? ctx.getRisorsaTipo() : tipoDefault,
                ctx != null ? ctx.getRisorsaId() : null,
                ctx != null ? ctx.getPazienteId() : null,
                status, request);
    }

    static AuditLog.AzioneAudit azione(String method, String path) {
        if (path.endsWith("/download")) return AuditLog.AzioneAudit.DOWNLOAD;
        // Le ricerche avanzate sono POST ma sono letture
        if (path.endsWith("/advanced-search")) return AuditLog.AzioneAudit.LETTURA;
        return switch (method) {
            case "POST" -> AuditLog.AzioneAudit.CREAZIONE;
            case "PUT", "PATCH" -> AuditLog.AzioneAudit.MODIFICA;
            case "DELETE" -> AuditLog.AzioneAudit.ELIMINAZIONE;
            default -> AuditLog.AzioneAudit.LETTURA;
        };
    }
}
