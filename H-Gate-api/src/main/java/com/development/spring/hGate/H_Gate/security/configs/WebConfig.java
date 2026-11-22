package com.development.spring.hGate.H_Gate.security.configs;

import com.development.spring.hGate.H_Gate.audit.AuditInterceptor;
import com.development.spring.hGate.H_Gate.shared.components.RequestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private RequestInterceptor requestInterceptor;

    @Autowired
    private AuditInterceptor auditInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestInterceptor);
        // Registro accessi ai dati clinici (GDPR)
        registry.addInterceptor(auditInterceptor);
    }
}

