package com.development.spring.hGate.H_Gate.libs.web.controllers;

import com.development.spring.hGate.H_Gate.libs.data.models.IdentifiableEntity;
import com.development.spring.hGate.H_Gate.libs.web.mappers.TransferableObjectMapper;
import com.development.spring.hGate.H_Gate.libs.web.services.ReadService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface ReadController<Model extends IdentifiableEntity<Id>, Id, ResponseDTO> {

    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    TransferableObjectMapper<Model, ?, ResponseDTO> getReadMapper();
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    ReadService<Model, Id> getReadService();
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    @GetMapping({"/{id}"})
    default ResponseDTO getById(@PathVariable Id id) {
        Optional<Model> model = getReadService().getById(id);
        if (model.isEmpty()) {
            String message = String.format("Resource with id %s not found.", id.toString());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, message);
        }
        Objects.requireNonNull(getReadMapper());
        return model.<ResponseDTO>map(getReadMapper()::mapModelToResponse).orElse(null);
    }
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    @GetMapping
    default List<ResponseDTO> getAll() {
        return getReadMapper()
                .mapModelsToResponseList(getReadService().getAll());
    }
}