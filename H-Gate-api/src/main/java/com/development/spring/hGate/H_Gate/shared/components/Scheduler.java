package com.development.spring.hGate.H_Gate.shared.components;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class Scheduler {

    @Autowired
    private EnvironmentService environmentService;

    @PostConstruct
    public void postConstruct() {

    }

}