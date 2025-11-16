package com.development.spring.hGate.H_Gate.libs.web.mappers;


import com.development.spring.hGate.H_Gate.libs.web.dtos.PageDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TransferableObjectMapper<Model, RequestDTO, ResponseDTO> {
    Model mapRequestToModel(RequestDTO paramRequestDTO);

    ResponseDTO mapModelToResponse(Model paramModel);

    List<ResponseDTO> mapModelsToResponseList(List<Model> paramList);

    default PageDTO<ResponseDTO> mapModelsPageToResponsePage(Page<Model> modelsPage) {
        return (PageDTO<ResponseDTO>) PageDTO.builder()
                .content((List<Object>) mapModelsToResponseList(modelsPage.getContent()))
                .first(modelsPage.isFirst())
                .last(modelsPage.isLast())
                .number(modelsPage.getNumber())
                .numberOfElements(modelsPage.getNumberOfElements())
                .size(modelsPage.getSize())
                .sort(modelsPage.getSort().toList())
                .totalElements(modelsPage.getTotalElements())
                .totalPages(modelsPage.getTotalPages())
                .build();
    }
}
