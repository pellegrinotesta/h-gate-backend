package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.dtos.medici.ParametriVitaliDTO;
import com.development.spring.hGate.H_Gate.dtos.pazienti.PercorsiTerapeuticiDTO;
import com.development.spring.hGate.H_Gate.dtos.pazienti.ValutazionePsicologicaDTO;
import com.development.spring.hGate.H_Gate.entity.Allegato;
import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.entity.Paziente;
import com.development.spring.hGate.H_Gate.entity.Referto;
import com.development.spring.hGate.H_Gate.entity.Users;
import com.development.spring.hGate.H_Gate.mappers.RefertoMapper;
import com.development.spring.hGate.H_Gate.repositories.UserRepository;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.shared.models.Role;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Genera il PDF ufficiale della cartella clinica di un paziente.
 * <p>
 * I dati vengono letti con gli stessi servizi degli endpoint di consultazione; l'autorizzazione
 * ({@code @access.paziente}) e la registrazione nel registro accessi avvengono nel controller.
 * Il layout è nel template Thymeleaf {@code templates/pdf/cartella-clinica.html}, convertito in PDF
 * con OpenHTMLtoPDF (CSS 2.1 + paged media: niente flex/grid, layout con tabelle).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CartellaClinicaPdfService {

    private static final ZoneId ZONA = ZoneId.of("Europe/Rome");
    private static final Locale IT = Locale.ITALY;
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy", IT);
    private static final DateTimeFormatter DATA_ORA = DateTimeFormatter.ofPattern("dd/MM/yyyy 'alle' HH:mm", IT);
    private static final DateTimeFormatter FILE = DateTimeFormatter.ofPattern("yyyyMMdd", IT);
    private static final ObjectMapper JSON = new ObjectMapper();

    private final PazienteService pazienteService;
    private final RefertoService refertoService;
    private final RefertoMapper refertoMapper;
    private final PercorsiTerapeuticiService percorsiTerapeuticiService;
    private final ValutazionePsicologicaService valutazionePsicologicaService;
    private final AllegatoService allegatoService;
    private final UserRepository userRepository;
    private final ITemplateEngine templateEngine;

    /** PDF generato e nome del file da proporre al download. */
    public record CartellaPdf(byte[] contenuto, String nomeFile) {}

    @Transactional(readOnly = true)
    public CartellaPdf genera(Integer pazienteId, JwtAuthentication auth) {
        Paziente paziente = pazienteService.getById(pazienteId);
        LocalDateTime adesso = LocalDateTime.now(ZONA);

        Context ctx = new Context(IT);
        ctx.setVariable("logo", logoDataUri());
        ctx.setVariable("numeroCartella", String.format("%06d", paziente.getId()));
        ctx.setVariable("emissione", adesso.format(DATA_ORA));
        ctx.setVariable("emessaDa", emessaDa(auth));
        ctx.setVariable("paziente", pazienteVista(paziente));
        ctx.setVariable("referti", refertoService.listaRefertiPaziente(pazienteId).stream().map(this::refertoVista).toList());
        ctx.setVariable("valutazioni", valutazionePsicologicaService.getByPaziente(pazienteId).stream().map(this::valutazioneVista).toList());
        ctx.setVariable("percorsi", percorsiTerapeuticiService.getByPaziente(pazienteId).stream().map(this::percorsoVista).toList());
        ctx.setVariable("allegati", allegatoService.getByPaziente(pazienteId, auth).stream().map(this::allegatoVista).toList());

        String html = templateEngine.process("pdf/cartella-clinica", ctx);
        byte[] pdf = render(html);

        String nomeFile = "Cartella_clinica_" + perNomeFile(paziente.getCognome()) + "_" + perNomeFile(paziente.getNome())
                + "_" + adesso.format(FILE) + ".pdf";
        return new CartellaPdf(pdf, nomeFile);
    }

    // ------------------------------------------------------------------ render

    private byte[] render(String html) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(out);
            builder.run();
            return out.toByteArray();
        } catch (Exception e) {
            log.error("Errore nella generazione del PDF della cartella clinica", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Impossibile generare il PDF della cartella clinica");
        }
    }

    private String logoDataUri() {
        try (InputStream in = new ClassPathResource("pdf/logo-mark.png").getInputStream()) {
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(in.readAllBytes());
        } catch (IOException e) {
            log.warn("Logo per il PDF non trovato, il documento verrà generato senza logo");
            return null;
        }
    }

    // ------------------------------------------------------------------ viste per il template

    @Value @Builder
    public static class Campo { String etichetta; String valore; boolean evidenza; }

    @Value @Builder
    public static class PazienteVista {
        String nomeCompleto; String codiceFiscale; String dataNascita; String eta; String sesso; String residenza;
        String gruppoSanguigno; String altezza; String peso; String allergie; boolean allergiePresenti;
        String patologie; String note;
    }

    @Value @Builder
    public static class RefertoVista {
        String titolo; String data; String tipo; String medico; String qualifica; String firma; boolean firmato;
        List<Campo> campi;
    }

    @Value @Builder
    public static class ValutazioneVista {
        String tipoTest; String data; String medico; Map<String, String> punteggi; String punteggiTesto; String interpretazione;
    }

    @Value @Builder
    public static class PercorsoVista {
        String titolo; String obiettivi; String medico; String inizio; String finePrevista; String sedute; String stato;
    }

    @Value @Builder
    public static class AllegatoVista { String nome; String descrizione; String tipo; String dimensione; String caricato; }

    private PazienteVista pazienteVista(Paziente p) {
        LocalDate nascita = toLocalDate(p.getDataNascita());
        return PazienteVista.builder()
                .nomeCompleto(maiuscolo(p.getCognome()) + " " + testo(p.getNome()))
                .codiceFiscale(testo(p.getCodiceFiscale()))
                .dataNascita(nascita != null ? nascita.format(DATA) : "—")
                .eta(nascita != null ? Period.between(nascita, LocalDate.now(ZONA)).getYears() + " anni" : null)
                .sesso("M".equalsIgnoreCase(p.getSesso()) ? "Maschile" : "F".equalsIgnoreCase(p.getSesso()) ? "Femminile" : testo(p.getSesso()))
                .residenza(testo(p.getCitta()))
                .gruppoSanguigno(testo(p.getGruppoSanguigno()))
                .altezza(p.getAltezzaCm() != null ? p.getAltezzaCm() + " cm" : "—")
                .peso(p.getPesoKg() != null ? p.getPesoKg().stripTrailingZeros().toPlainString().replace('.', ',') + " kg" : "—")
                .allergie(haValore(p.getAllergie()) ? p.getAllergie().trim() : "Nessuna allergia nota")
                .allergiePresenti(haValore(p.getAllergie()))
                .patologie(haValore(p.getPatologieCroniche()) ? p.getPatologieCroniche().trim() : "Nessuna")
                .note(haValore(p.getNoteMediche()) ? p.getNoteMediche().trim() : null)
                .build();
    }

    private RefertoVista refertoVista(Referto r) {
        Medico m = r.getMedico();
        Users mu = m != null ? m.getUser() : null;
        String qualifica = null;
        if (m != null) {
            List<String> parti = new ArrayList<>();
            if (haValore(m.getSpecializzazione())) parti.add(m.getSpecializzazione());
            if (haValore(m.getNumeroAlbo())) parti.add("Albo n. " + m.getNumeroAlbo());
            qualifica = parti.isEmpty() ? null : String.join(" · ", parti);
        }

        List<Campo> campi = new ArrayList<>();
        aggiungi(campi, "Anamnesi", r.getAnamnesi(), false);
        aggiungi(campi, "Esame obiettivo", r.getEsameObiettivo(), false);
        aggiungi(campi, "Parametri vitali", parametriVitali(r), false);
        campi.add(Campo.builder().etichetta("Diagnosi").valore(haValore(r.getDiagnosi()) ? r.getDiagnosi().trim() : "—").evidenza(true).build());
        aggiungi(campi, "Terapia", r.getTerapia(), false);
        aggiungi(campi, "Prescrizioni", r.getPrescrizioni(), false);
        aggiungi(campi, "Esami richiesti", r.getEsamiRichiesti(), false);
        aggiungi(campi, "Prossimo controllo", r.getProssimoControllo() != null ? r.getProssimoControllo().format(DATA) : null, false);
        aggiungi(campi, "Note", r.getNoteMediche(), false);

        boolean firmato = Boolean.TRUE.equals(r.getIsFirmato());
        return RefertoVista.builder()
                .titolo(testo(r.getTitolo()))
                .data(r.getDataEmissione() != null ? r.getDataEmissione().format(DATA) : "—")
                .tipo(testo(r.getTipoReferto()))
                .medico(mu != null ? (testo(mu.getNome()) + " " + testo(mu.getCognome())).trim() : null)
                .qualifica(qualifica)
                .firmato(firmato)
                .firma(firmato
                        ? "Firmato digitalmente" + (r.getDataFirma() != null ? " il " + r.getDataFirma().format(DATA) : "")
                        : "Non firmato")
                .campi(campi)
                .build();
    }

    private String parametriVitali(Referto r) {
        ParametriVitaliDTO p;
        try {
            p = refertoMapper.convertModelToDTO(r).getParametriVitali();
        } catch (Exception e) {
            return null;
        }
        if (p == null) return null;
        List<String> parti = new ArrayList<>();
        if (p.getPressioneSistolica() != null && p.getPressioneDiastolica() != null)
            parti.add("PA " + p.getPressioneSistolica() + "/" + p.getPressioneDiastolica() + " mmHg");
        if (p.getFrequenzaCardiaca() != null) parti.add("FC " + p.getFrequenzaCardiaca() + " bpm");
        if (p.getTemperatura() != null) parti.add("T " + numero(p.getTemperatura()) + " °C");
        if (p.getSaturazione() != null) parti.add("SpO2 " + p.getSaturazione() + "%");
        if (p.getPeso() != null) parti.add("Peso " + numero(p.getPeso()) + " kg");
        if (p.getAltezza() != null) parti.add("Altezza " + p.getAltezza() + " cm");
        return parti.isEmpty() ? null : String.join("   ·   ", parti);
    }

    private ValutazioneVista valutazioneVista(ValutazionePsicologicaDTO v) {
        Map<String, String> punteggi = null;
        String punteggiTesto = null;
        if (haValore(v.getPunteggi())) {
            try {
                Map<String, Object> grezzi = JSON.readValue(v.getPunteggi(), new TypeReference<LinkedHashMap<String, Object>>() {});
                punteggi = new LinkedHashMap<>();
                for (Map.Entry<String, Object> e : grezzi.entrySet()) {
                    punteggi.put(e.getKey(), e.getValue() == null ? "—" : String.valueOf(e.getValue()));
                }
            } catch (Exception e) {
                punteggiTesto = v.getPunteggi();
            }
        }
        return ValutazioneVista.builder()
                .tipoTest(testo(v.getTipoTest()))
                .data(v.getDataValutazione() != null ? v.getDataValutazione().format(DATA) : "—")
                .medico(nomeMedico(v.getMedicoNome(), v.getMedicoCognome()))
                .punteggi(punteggi == null || punteggi.isEmpty() ? null : punteggi)
                .punteggiTesto(punteggiTesto)
                .interpretazione(haValore(v.getInterpretazione()) ? v.getInterpretazione().trim() : null)
                .build();
    }

    private PercorsoVista percorsoVista(PercorsiTerapeuticiDTO p) {
        String stato = p.getStato() == null ? "—"
                : p.getStato().substring(0, 1).toUpperCase(IT) + p.getStato().substring(1).toLowerCase(IT);
        return PercorsoVista.builder()
                .titolo(testo(p.getTitolo()))
                .obiettivi(haValore(p.getObiettivi()) ? p.getObiettivi().trim() : null)
                .medico(nomeMedico(p.getMedicoNome(), p.getMedicoCognome()))
                .inizio(p.getDataInizio() != null ? p.getDataInizio().format(DATA) : "—")
                .finePrevista(p.getDataFinePrevista() != null ? p.getDataFinePrevista().format(DATA) : "—")
                .sedute((p.getNumeroSeduteEffettuate() != null ? p.getNumeroSeduteEffettuate() : 0) + " / "
                        + (p.getNumeroSedutePreviste() != null ? p.getNumeroSedutePreviste() : "—"))
                .stato(stato)
                .build();
    }

    private AllegatoVista allegatoVista(Allegato a) {
        return AllegatoVista.builder()
                .nome(testo(a.getNomeFile()))
                .descrizione(haValore(a.getDescrizione()) ? a.getDescrizione().trim() : null)
                .tipo(testo(a.getTipoFile() != null ? a.getTipoFile().toString() : null))
                .dimensione(dimensione(a.getSizeBytes()))
                .caricato(a.getUploadedAt() != null ? a.getUploadedAt().format(DATA) : "—")
                .build();
    }

    private String emessaDa(JwtAuthentication auth) {
        if (auth == null) return null;
        String ruolo = auth.hasRole(Role.MEDICO) ? "Specialista"
                : auth.hasRole(Role.ADMIN) ? "Amministratore"
                : auth.hasRole(Role.TUTORE) ? "Tutore legale" : null;
        String nome = userRepository.findById(auth.getId())
                .map(u -> (testo(u.getNome()) + " " + testo(u.getCognome())).trim())
                .orElse(auth.getUsername());
        return ruolo != null ? nome + " (" + ruolo + ")" : nome;
    }

    // ------------------------------------------------------------------ utilità

    private static void aggiungi(List<Campo> campi, String etichetta, String valore, boolean evidenza) {
        if (haValore(valore)) {
            campi.add(Campo.builder().etichetta(etichetta).valore(valore.trim()).evidenza(evidenza).build());
        }
    }

    /** True se il testo contiene un'informazione reale (non vuoto, né "nessuna", "-", ...). */
    static boolean haValore(String s) {
        if (s == null) return false;
        String t = s.trim().toLowerCase(IT);
        return !t.isEmpty() && !List.of("nessuna", "nessuno", "no", "-", "—", "n.d.", "nd").contains(t);
    }

    private static String testo(String s) {
        return s == null || s.isBlank() ? "—" : s.trim();
    }

    private static String maiuscolo(String s) {
        return s == null ? "" : s.trim().toUpperCase(IT);
    }

    private static String nomeMedico(String nome, String cognome) {
        String n = ((nome == null ? "" : nome) + " " + (cognome == null ? "" : cognome)).trim();
        return n.isEmpty() ? "—" : n;
    }

    private static String numero(Double d) {
        return (d % 1 == 0 ? String.valueOf(d.longValue()) : String.valueOf(d)).replace('.', ',');
    }

    private static String dimensione(Long bytes) {
        if (bytes == null || bytes <= 0) return "—";
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return Math.round(bytes / 1024.0) + " KB";
        return String.format(IT, "%.1f MB", bytes / (1024.0 * 1024.0));
    }

    private static LocalDate toLocalDate(Date d) {
        if (d == null) return null;
        if (d instanceof java.sql.Date sql) return sql.toLocalDate();
        return d.toInstant().atZone(ZONA).toLocalDate();
    }

    /** Nome sicuro per il file: senza accenti, spazi o caratteri speciali. */
    private static String perNomeFile(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return n.trim().replaceAll("[^A-Za-z0-9]+", "_").replaceAll("^_+|_+$", "");
    }
}
