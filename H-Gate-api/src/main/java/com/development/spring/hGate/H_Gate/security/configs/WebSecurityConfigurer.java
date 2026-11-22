package com.development.spring.hGate.H_Gate.security.configs;

import com.development.spring.hGate.H_Gate.security.filters.AuthorizationFilter;
import com.development.spring.hGate.H_Gate.security.services.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfigurer {

    private final List<String> allowedOrigins;
    private final UserDetailsService userDetailsService;
    private final AuthorizationFilter authorizationFilter;
    private final Logger logger = LoggerFactory.getLogger(WebSecurityConfigurer.class);

    public WebSecurityConfigurer(
            UserDetailsService userDetailsService,
            AuthorizationFilter authorizationFilter,
            @Value("${application.cors.allowedOrigins}") List<String> allowedOrigins,
            SessionService sessionService) {
        this.allowedOrigins = allowedOrigins;
        this.userDetailsService = userDetailsService;
        this.authorizationFilter = authorizationFilter;
        if (logger.isInfoEnabled()) {
            logger.info("Allowed Origins: {}", allowedOrigins.toString());
        }
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService);
        return authenticationManagerBuilder.build();
    }


    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/authentication", "/registration", "/version",
                                "/password/**", "/error", "/*/imports/events",
                                "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )

                // 401 se manca il token o non è valido/scaduto, 403 se l'utente non ha i permessi:
                // il frontend usa il 401 per riportare l'utente al login
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, e) -> writeJsonError(response,
                                HttpServletResponse.SC_UNAUTHORIZED,
                                "Sessione scaduta o non valida: effettua di nuovo l'accesso."))
                        .accessDeniedHandler((request, response, e) -> writeJsonError(response,
                                HttpServletResponse.SC_FORBIDDEN,
                                "Non hai i permessi per accedere a questa risorsa.")))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    private static void writeJsonError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        ResponseDTO<Void> body = new ResponseDTO<>();
        body.setOk(false);
        body.setMessage(message);
        JSON.writeValue(response.getWriter(), body);
    }

    private static final ObjectMapper JSON = new ObjectMapper();

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final var corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(allowedOrigins);
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setMaxAge(1800L); // 30 min
        final var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

}