package com.development.spring.hGate.H_Gate.libs.web.services;


import com.development.spring.hGate.H_Gate.libs.data.models.IdentifiableEntity;
import com.development.spring.hGate.H_Gate.libs.data.repositories.CrudRepository;

public interface RepositoryService<Model extends IdentifiableEntity<Id>, Id> {
    CrudRepository<Model, Id> getRepository();
}
