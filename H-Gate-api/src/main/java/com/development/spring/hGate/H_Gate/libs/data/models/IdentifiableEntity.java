package com.development.spring.hGate.H_Gate.libs.data.models;

public interface IdentifiableEntity<Id> {
    Id getId();

    void setId(Id paramId);
}
