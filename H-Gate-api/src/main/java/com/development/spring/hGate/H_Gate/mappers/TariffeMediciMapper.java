package com.development.spring.hGate.H_Gate.mappers;

import com.development.spring.hGate.H_Gate.dtos.medici.TariffeMediciDTO;
import com.development.spring.hGate.H_Gate.entity.TariffeMedici;
import com.development.spring.hGate.H_Gate.libs.web.dtos.PageDTO;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface TariffeMediciMapper {

    TariffeMediciDTO convertModelToDTO(TariffeMedici tariffeMedici);

    TariffeMedici convertDtoToModel(TariffeMediciDTO tariffeMediciDTO);

    List<TariffeMediciDTO> convertModelsToDtos(List<TariffeMedici> users);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModel(TariffeMedici source, @MappingTarget TariffeMedici target);

    default PageDTO<TariffeMediciDTO> convertModelsPageToDtosPage(Page<TariffeMedici> modelsPage) {
        return PageDTO.<TariffeMediciDTO>builder()
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
