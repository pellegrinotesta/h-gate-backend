package com.development.spring.hGate.H_Gate.libs.web.controllers;


import com.development.spring.hGate.H_Gate.libs.data.models.IdentifiableEntity;
import com.development.spring.hGate.H_Gate.libs.web.services.*;
import org.springframework.security.access.prepost.PreAuthorize;

public interface CrudController<Model extends IdentifiableEntity<Id>, Id, CreateRequestDTO, CreateResponseDTO, ReadResponseDTO, SearchRequestDTO, SearchResponseDTO> extends CreateController<Model, Id, CreateRequestDTO, CreateResponseDTO>, ReadController<Model, Id, ReadResponseDTO>, UpdateController<Model, Id, CreateRequestDTO, CreateResponseDTO>, DeleteController<Model, Id>, SearchController<Model, SearchRequestDTO, SearchResponseDTO>, AdvancedSearchController<Model, SearchResponseDTO> {

    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    CrudService<Model, Id, SearchRequestDTO> getCrudService();
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    default CreateService<Model, Id> getCreateService() {
        return (CreateService)getCrudService();
    }
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    default DeleteService<Model, Id> getDeleteService() {
        return (DeleteService)getCrudService();
    }
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    default ReadService<Model, Id> getReadService() {
        return (ReadService)getCrudService();
    }
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    default UpdateService<Model, Id> getUpdateService() {
        return (UpdateService)getCrudService();
    }
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    default SearchService<Model, SearchRequestDTO> getSearchService() {
        return (SearchService)getCrudService();
    }
    @PreAuthorize("hasAuthority('ADMIN') or @roleService.canRead(#root.this.moduleName, #root.this.sessionService.getRole())")
    default AdvancedSearchService<Model> getAdvancedSearchService() {
        return (AdvancedSearchService)getCrudService();
    }
}
