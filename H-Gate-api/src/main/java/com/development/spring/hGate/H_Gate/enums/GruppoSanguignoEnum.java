package com.development.spring.hGate.H_Gate.enums;

import lombok.Getter;

@Getter
public enum GruppoSanguignoEnum {
    A_POSITIVO("A+"),
    A_NEGATIVO("A-"),
    B_POSITIVO("B+"),
    B_NEGATIVO("B-"),
    AB_POSITIVO("AB+"),
    AB_NEGATIVO("AB-"),
    ZERO_POSITIVO("0+"),
    ZERO_NEGATIVO("0-");

    private final String valore;

    GruppoSanguignoEnum(String valore) {
        this.valore = valore;
    }

}