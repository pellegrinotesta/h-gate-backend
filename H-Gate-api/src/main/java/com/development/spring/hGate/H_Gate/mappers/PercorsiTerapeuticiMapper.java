package com.development.spring.hGate.H_Gate.mappers;

import com.development.spring.hGate.H_Gate.dtos.pazienti.PercorsiTerapeuticiDTO;
import com.development.spring.hGate.H_Gate.entity.PercorsiTerapeutici;
import com.development.spring.hGate.H_Gate.libs.web.dtos.PageDTO;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface PercorsiTerapeuticiMapper {

    PercorsiTerapeuticiDTO convertModelToDTO(PercorsiTerapeutici percorsoTerapeutico);

    PercorsiTerapeutici convertDtoToModel(PercorsiTerapeuticiDTO percorsiTerapeuticiDTO);

    List<PercorsiTerapeuticiDTO> convertModelsToDtos(List<PercorsiTerapeutici> percorsiTerapeuticis);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModel(PercorsiTerapeutici source, @MappingTarget PercorsiTerapeutici target);

    default PageDTO<PercorsiTerapeuticiDTO> convertModelsPageToDtosPage(Page<PercorsiTerapeutici> modelsPage) {
        return PageDTO.<PercorsiTerapeuticiDTO>builder()
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
