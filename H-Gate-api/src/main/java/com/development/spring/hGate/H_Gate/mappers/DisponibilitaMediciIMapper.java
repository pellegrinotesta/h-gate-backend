package com.development.spring.hGate.H_Gate.mappers;

import com.development.spring.hGate.H_Gate.dtos.medici.DisponibilitaMediciDTO;
import com.development.spring.hGate.H_Gate.entity.DisponibilitaMedico;
import com.development.spring.hGate.H_Gate.libs.web.dtos.PageDTO;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface DisponibilitaMediciIMapper {

    DisponibilitaMediciDTO convertModelToDTO(DisponibilitaMedico disponibilitaMedico);

    DisponibilitaMedico convertDtoToModel(DisponibilitaMediciDTO disponibilitaMediciDTO);

    List<DisponibilitaMediciDTO> convertModelsToDtos(List<DisponibilitaMedico> disponibilitaMedici);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModel(DisponibilitaMedico source, @MappingTarget DisponibilitaMedico target);

    default PageDTO<DisponibilitaMediciDTO> convertModelsPageToDtosPage(Page<DisponibilitaMedico> modelsPage) {
        return PageDTO.<DisponibilitaMediciDTO>builder()
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
