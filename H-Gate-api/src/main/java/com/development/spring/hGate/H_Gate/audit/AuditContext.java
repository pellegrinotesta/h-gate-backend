package com.development.spring.hGate.H_Gate.audit;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Informazioni sulla risorsa clinica coinvolta nella richiesta corrente, raccolte durante
 * i controlli di accesso e scritte nel registro a fine richiesta da {@link AuditInterceptor}.
 * <p>
 * Vale la prima risorsa impostata (la più specifica, es. il referto), mentre il paziente
 * viene ricavato dai controlli annidati.
 */
public final class AuditContext {

    private static final String ATTR = AuditContext.class.getName();

    private String risorsaTipo;
    private Integer risorsaId;
    private Integer pazienteId;

    private AuditContext() {}

    public static void risorsa(String tipo, Integer id) {
        AuditContext ctx = current();
        if (ctx != null && ctx.risorsaTipo == null) {
            ctx.risorsaTipo = tipo;
            ctx.risorsaId = id;
        }
    }

    /**
     * Come {@link #risorsa}, ma sovrascrive un tipo già impostato (es. dal controllo di accesso).
     * Da usare nel controller quando l'operazione è più specifica della risorsa controllata,
     * es. l'esportazione della cartella clinica di un paziente.
     */
    public static void sovrascriviRisorsa(String tipo, Integer id) {
        AuditContext ctx = current();
        if (ctx != null) {
            ctx.risorsaTipo = tipo;
            ctx.risorsaId = id;
        }
    }

    public static void paziente(Integer pazienteId) {
        AuditContext ctx = current();
        if (ctx != null && ctx.pazienteId == null && pazienteId != null) {
            ctx.pazienteId = pazienteId;
        }
    }

    static AuditContext current() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (attrs == null) return null;
        AuditContext ctx = (AuditContext) attrs.getAttribute(ATTR, RequestAttributes.SCOPE_REQUEST);
        if (ctx == null) {
            ctx = new AuditContext();
            attrs.setAttribute(ATTR, ctx, RequestAttributes.SCOPE_REQUEST);
        }
        return ctx;
    }

    String getRisorsaTipo() { return risorsaTipo; }
    Integer getRisorsaId() { return risorsaId; }
    Integer getPazienteId() { return pazienteId; }
}
