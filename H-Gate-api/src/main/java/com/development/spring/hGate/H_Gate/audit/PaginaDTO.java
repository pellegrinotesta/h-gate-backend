package com.development.spring.hGate.H_Gate.audit;

import org.springframework.data.domain.Page;

import java.util.List;

/** Pagina di risultati con un formato JSON stabile. */
public record PaginaDTO<T>(List<T> content, long totalElements, int page, int size, int totalPages) {
    public static <T> PaginaDTO<T> of(Page<T> p) {
        return new PaginaDTO<>(p.getContent(), p.getTotalElements(), p.getNumber(), p.getSize(), p.getTotalPages());
    }
}
