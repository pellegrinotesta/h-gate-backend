package com.development.spring.hGate.H_Gate.mappers;

import com.development.spring.hGate.H_Gate.dtos.RefertoDTO;
import com.development.spring.hGate.H_Gate.entity.Referto;
import com.development.spring.hGate.H_Gate.libs.web.dtos.PageDTO;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface RefertoMapper {

    RefertoDTO convertModelToDTO(Referto referto);

    Referto convertDtoToModel(RefertoDTO refertoDTO);

    List<RefertoDTO> convertModelsToDtos(List<Referto> referto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModel(Referto source, @MappingTarget Referto target);

    default PageDTO<RefertoDTO> convertModelsPageToDtosPage(Page<Referto> modelsPage) {
        return PageDTO.<RefertoDTO>builder()
                .content(convertModelsToDtos(modelsPage.getContent()))
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
