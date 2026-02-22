package com.development.spring.hGate.H_Gate.dtos;

import com.development.spring.hGate.H_Gate.libs.web.dtos.PageDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponseData<T> {
    private long count;
    private String next;
    private String previous;
    private T results;

    public static <T> PaginatedResponseData<T> fromPage(Page<T> page, String baseUrl) {
        int currentPage = page.getNumber();
        int totalPages = page.getTotalPages();
        long totalElements = page.getTotalElements();
        int pageSize = page.getSize();

        String next = null;
        if (currentPage + 1 < totalPages) {
            next = String.format("%s?page=%d&size=%d", baseUrl, currentPage + 1, pageSize);
        }

        String previous = null;
        if (currentPage > 0) {
            previous = String.format("%s?page=%d&size=%d", baseUrl, currentPage - 1, pageSize);
        }

        return PaginatedResponseData.<T>builder()
                .count(totalElements)
                .next(next)
                .previous(previous)
                .results((T) page.getContent())
                .build();
    }

    /**
     * Crea PaginatedResponseData da PageDTO personalizzato
     */
    public static <T> PaginatedResponseData<T> fromPageDTO(PageDTO<T> pageDTO) {
        int currentPage = pageDTO.getNumber();
        int totalPages = pageDTO.getTotalPages();

        String next = null;
        if (currentPage + 1 < totalPages) {
            next = ServletUriComponentsBuilder.fromCurrentRequest()
                    .replaceQueryParam("page", currentPage + 1)
                    .replaceQueryParam("size", pageDTO.getSize())
                    .toUriString();
        }

        String previous = null;
        if (currentPage > 0) {
            previous = ServletUriComponentsBuilder.fromCurrentRequest()
                    .replaceQueryParam("page", currentPage - 1)
                    .replaceQueryParam("size", pageDTO.getSize())
                    .toUriString();
        }

        return PaginatedResponseData.<T>builder()
                .count(pageDTO.getTotalElements())
                .next(next)
                .previous(previous)
                .results((T) pageDTO.getContent())
                .build();
    }
}
