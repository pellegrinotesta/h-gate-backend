package com.development.spring.hGate.H_Gate.support;

import com.development.spring.hGate.H_Gate.entity.*;
import com.development.spring.hGate.H_Gate.entity.identifies.PazienteTutoreId;
import com.development.spring.hGate.H_Gate.enums.StatoPrenotazioneEnum;
import com.development.spring.hGate.H_Gate.repositories.*;
import com.development.spring.hGate.H_Gate.security.services.JwtService;
import com.development.spring.hGate.H_Gate.security.services.LoginAttemptService;
import com.development.spring.hGate.H_Gate.shared.models.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * Base per i test di integrazione: contesto Spring completo, H2 in memoria, MockMvc.
 * Ogni test gira in una transazione annullata alla fine, quindi i dati non si accumulano.
 * <p>
 * Scenario (uguale per tutti i test):
 * <ul>
 *   <li>{@code tutoreA} è genitore di {@code pazienteA}; {@code tutoreB} di {@code pazienteB}</li>
 *   <li>{@code medicoA} ha una prenotazione (con referto) con {@code pazienteA}</li>
 *   <li>{@code medicoSenzaPazienti} è approvato ma non ha pazienti in carico</li>
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class IntegrationTestBase {

    protected static final String PASSWORD = "Password123!";

    @Autowired protected MockMvc mvc;
    @Autowired protected ObjectMapper json;
    @Autowired protected UserRepository userRepository;
    @Autowired protected MedicoRepository medicoRepository;
    @Autowired protected PazienteRepository pazienteRepository;
    @Autowired protected TutoreLegaleRepository tutoreRepository;
    @Autowired protected PazienteTutoreRepository pazienteTutoreRepository;
    @Autowired protected PrenotazioneRepository prenotazioneRepository;
    @Autowired protected RefertoRepository refertoRepository;
    @Autowired protected TariffeMediciRepository tariffeRepository;
    @Autowired protected DisponibilitaMediciRepository disponibilitaRepository;
    @Autowired protected PasswordEncoder passwordEncoder;
    @Autowired protected UserDetailsService userDetailsService;
    @Autowired protected JwtService jwtService;
    @Autowired protected LoginAttemptService loginAttemptService;

    protected Users admin;
    protected Users tutoreAUser, tutoreBUser;
    protected TutoreLegale tutoreA, tutoreB;
    protected Paziente pazienteA, pazienteB;
    protected Medico medicoA, medicoSenzaPazienti;
    protected Prenotazione prenotazioneA;
    protected Referto refertoA;

    @BeforeEach
    void creaScenario() {
        admin = utente("admin@test.it", Role.ADMIN, true);

        tutoreAUser = utente("tutore.a@test.it", Role.TUTORE, true);
        tutoreBUser = utente("tutore.b@test.it", Role.TUTORE, true);
        tutoreA = tutoreRepository.save(TutoreLegale.builder().user(tutoreAUser).build());
        tutoreB = tutoreRepository.save(TutoreLegale.builder().user(tutoreBUser).build());

        pazienteA = paziente("Marco", "Verdi", "VRDMRC15A01H501A");
        pazienteB = paziente("Sofia", "Marino", "MRNSFO19B41F205B");
        collega(pazienteA, tutoreA, "MADRE");
        collega(pazienteB, tutoreB, "PADRE");

        medicoA = medico("medico.a@test.it", "ALBO-A", true);
        medicoSenzaPazienti = medico("medico.b@test.it", "ALBO-B", true);

        prenotazioneA = prenotazione(medicoA, pazienteA, tutoreAUser,
                LocalDateTime.now().plusDays(3).withHour(10).withMinute(0).withSecond(0).withNano(0),
                StatoPrenotazioneEnum.CONFERMATA);

        refertoA = refertoRepository.save(Referto.builder()
                .uuid(UUID.randomUUID().toString())
                .prenotazione(prenotazioneA)
                .medico(medicoA)
                .paziente(pazienteA)
                .dataEmissione(LocalDateTime.now())
                .tipoReferto("Visita")
                .titolo("Referto di prova")
                .diagnosi("Diagnosi di prova")
                .build());
    }

    // ------------------------------------------------------------- factory

    protected Users utente(String email, Role role, boolean verificato) {
        return userRepository.save(Users.builder()
                .uuid(UUID.randomUUID().toString())
                .email(email)
                .password(passwordEncoder.encode(PASSWORD))
                .nome("Nome")
                .cognome("Cognome")
                .isActive(true)
                .isVerified(verificato)
                .roles(new java.util.HashSet<>(Set.of(role)))
                .createdAt(new Date())
                .build());
    }

    protected Medico medico(String email, String albo, boolean approvato) {
        Users u = utente(email, Role.MEDICO, approvato);
        Medico m = medicoRepository.save(Medico.builder()
                .user(u)
                .specializzazione("Neuropsichiatra infantile")
                .numeroAlbo(albo)
                .build());
        tariffeRepository.save(TariffeMedici.builder()
                .medico(m).tipoVisita("Visita").costo(new BigDecimal("80.00"))
                .isPrimaVisita(false).isAttiva(true).build());
        return m;
    }

    protected Paziente paziente(String nome, String cognome, String cf) {
        return pazienteRepository.save(Paziente.builder()
                .nome(nome).cognome(cognome).codiceFiscale(cf)
                .dataNascita(Date.from(LocalDate.of(2015, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()))
                .sesso("M")
                .consensoPrivacy(true)
                .build());
    }

    protected void collega(Paziente p, TutoreLegale t, String relazione) {
        PazienteTutoreId id = new PazienteTutoreId();
        id.setPazienteId(p.getId());
        id.setTutoreId(t.getId());
        pazienteTutoreRepository.save(PazienteTutore.builder()
                .id(id).paziente(p).tutore(t).relazione(relazione).build());
    }

    protected Prenotazione prenotazione(Medico m, Paziente p, Users creataDa, LocalDateTime inizio, StatoPrenotazioneEnum stato) {
        return prenotazioneRepository.save(Prenotazione.builder()
                .uuid(UUID.randomUUID().toString())
                .numeroPrenotazione("T" + UUID.randomUUID().toString().substring(0, 12))
                .paziente(p)
                .medico(m)
                .createdByUserId(creataDa)
                .dataOra(inizio)
                .dataOraFine(inizio.plusMinutes(30))
                .tipoVisita("Visita")
                .stato(stato)
                .costo(new BigDecimal("80.00"))
                .build());
    }

    /** Token JWT valido per l'utente, come quello restituito dal login. */
    protected String token(Users user) {
        return jwtService.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));
    }

    protected String bearer(Users user) {
        return "Bearer " + token(user);
    }

    protected String toJson(Object o) throws Exception {
        return json.writeValueAsString(o);
    }
}
