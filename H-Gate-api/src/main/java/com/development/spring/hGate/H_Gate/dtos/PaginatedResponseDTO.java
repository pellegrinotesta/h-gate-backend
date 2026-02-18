package com.development.spring.hGate.H_Gate.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedResponseDTO<T> {

    private PaginatedResponseData<T> data;
    private String message;
    private int response_code;

    public static <T> PaginatedResponseDTO<T> success(PaginatedResponseData<T> data) {
        return PaginatedResponseDTO.<T>builder()
                .data(data)
                .message("Success")
                .response_code(200)
                .build();
    }

    public static <T> PaginatedResponseDTO<T> success(PaginatedResponseData<T> data, String message) {
        return PaginatedResponseDTO.<T>builder()
                .data(data)
                .message(message)
                .response_code(200)
                .build();
    }

}
