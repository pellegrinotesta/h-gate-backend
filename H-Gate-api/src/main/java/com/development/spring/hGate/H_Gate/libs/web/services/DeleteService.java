package com.development.spring.hGate.H_Gate.libs.web.services;


import com.development.spring.hGate.H_Gate.libs.data.models.IdentifiableEntity;

public interface DeleteService<Model extends IdentifiableEntity<Id>, Id> extends RepositoryService<Model, Id> {
    default boolean deleteById(Id id) {
        if (getRepository().existsById(id)) {
            getRepository().deleteById(id);
            return true;
        }
        return false;
    }
}
