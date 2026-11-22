package com.development.spring.hGate.H_Gate.security.services;

import com.development.spring.hGate.H_Gate.audit.AuditLog;
import com.development.spring.hGate.H_Gate.audit.AuditService;
import com.development.spring.hGate.H_Gate.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.shared.models.Role;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final LoginAttemptService loginAttemptService;

    private final JwtService jwtService;

    private final UserRepository userRepository;

    private final AuditService auditService;

    /** Registro accessi: anche i tentativi di login falliti o bloccati (senza mai la password). */
    private void auditLogin(AuditLog.AzioneAudit azione, String email) {
        HttpServletRequest request = null;
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
            request = attrs.getRequest();
        }
        AuditLog.EsitoAudit esito = azione == AuditLog.AzioneAudit.LOGIN ? AuditLog.EsitoAudit.OK : AuditLog.EsitoAudit.NEGATO;
        auditService.registra(azione, esito, null, email, "AUTENTICAZIONE", null, null, null, request);
    }

    public static final String MSG_MEDICO_IN_ATTESA =
            "Il tuo account è in attesa di approvazione da parte della clinica. Riceverai una email quando sarà attivo.";

    private Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    public Optional<String> authenticate(String username, String password) {
        if(loginAttemptService.isBlocked(username)) {
            auditLogin(AuditLog.AzioneAudit.LOGIN_BLOCCATO, username);
            throw buildUserIsBlockedException();
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                username,
                password);
        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            ensureMedicoApprovato(userDetails);
            String jwt = jwtService.generateToken(userDetails);
            auditLogin(AuditLog.AzioneAudit.LOGIN, username);
            logger.debug(String.format("Authentication succeeded for user %s", username));
            loginAttemptService.clearCache(username);

            return Optional.of(jwt);
        } catch (AuthenticationException ex) {
            loginAttemptService.loginFailed(username);
            auditLogin(AuditLog.AzioneAudit.LOGIN_FALLITO, username);

            // Non registrare mai la password nei log
            logger.debug("Authentication failed for user {} caused by {}.", username, ex.getMessage());
        }
        return Optional.empty();
    }


    public Optional<String> renewAuthentication(Authentication authentication) {
        if (!(authentication instanceof JwtAuthentication)) {
            logger.debug("Called renewAuthentication using an invalid authentication type.");
            return Optional.empty();
        }
        JwtAuthentication jwtAuthentication = (JwtAuthentication) authentication;
        if (!jwtService.isTokenAboutToExpire(jwtAuthentication)) {
            logger.debug("Called renewAuthentication before expiration date.");
            return Optional.empty();
        }
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(jwtAuthentication.getUsername());

           String jwt = jwtService.generateToken(userDetails);
            logger.debug(String.format("Authentication renewal succeeded for user %s",
                    jwtAuthentication.getUsername()));
            return Optional.of(jwt);
        } catch (UsernameNotFoundException ex) {
            logger.debug(String.format("Authentication renewal failed for user: %s with reason: %s",
                    jwtAuthentication.getUsername(),
                    ex.getMessage())
            );
            return Optional.empty();
        }
    }

    /** I medici registrati da soli non possono accedere finché un admin non li approva. */
    private void ensureMedicoApprovato(UserDetails userDetails) {
        boolean isMedico = userDetails.getAuthorities().stream()
                .anyMatch(a -> Role.MEDICO.name().equals(a.getAuthority()));
        if (!isMedico) return;
        boolean approvato = userRepository.findByEmail(userDetails.getUsername())
                .map(u -> Boolean.TRUE.equals(u.getIsVerified()))
                .orElse(false);
        if (!approvato) {
            auditLogin(AuditLog.AzioneAudit.LOGIN_FALLITO, userDetails.getUsername());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, MSG_MEDICO_IN_ATTESA);
        }
    }

    private ResponseStatusException buildUserIsBlockedException() {
        String message = "Troppi tentativi di accesso falliti. Riprova tra 15 minuti.";
        logger.debug("Login bloccato per troppi tentativi");
        return new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, message);
    }
}