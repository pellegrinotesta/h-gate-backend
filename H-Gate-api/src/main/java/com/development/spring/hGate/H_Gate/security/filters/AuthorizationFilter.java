package com.development.spring.hGate.H_Gate.security.filters;

import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.security.services.CryptoService;
import com.development.spring.hGate.H_Gate.security.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CryptoService cryptoService;

    private Authentication buildAuthenticationToken(String jwt, HttpServletRequest request) {
        JwtAuthentication jwtAuthentication = jwtService.deserialize(jwt);
        jwtAuthentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        jwtAuthentication.setAuthenticated(true);
        return null;
    }

    private Authentication getAuthenticationFromContext() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private void setAuthenticationInContext(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private boolean isAuthenticationNeeded(String jwt) {
        return jwt.length() > 0 &&
                getAuthenticationFromContext() == null &&
               jwtService.isTokenWellFormed(jwt) &&
               jwtService.isTokenNotExpired(jwt);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String encryptedJwt = authorizationHeader.substring(7);
            String jwt = cryptoService.decrypt(encryptedJwt);
            if (isAuthenticationNeeded(jwt)) {
                setAuthenticationInContext(buildAuthenticationToken(jwt, request));
            }
        }
        filterChain.doFilter(request, response);
   }
}