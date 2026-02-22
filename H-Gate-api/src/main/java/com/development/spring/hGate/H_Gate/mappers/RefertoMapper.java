package com.development.spring.hGate.H_Gate.mappers;

import com.development.spring.hGate.H_Gate.dtos.medici.RefertoCreateDTO;
import com.development.spring.hGate.H_Gate.dtos.medici.RefertoDTO;
import com.development.spring.hGate.H_Gate.dtos.medici.RefertoUpdateDTO;
import com.development.spring.hGate.H_Gate.entity.Referto;
import com.development.spring.hGate.H_Gate.libs.web.dtos.PageDTO;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface RefertoMapper {

    // parametriVitali ignorato: è String sull'entity, Object nel DTO
    // la conversione è gestita dal RefertoService con ObjectMapper
    @Mapping(target = "parametriVitali", ignore = true)
    RefertoDTO convertModelToDTO(Referto referto);

    @Mapping(target = "parametriVitali", ignore = true)
    Referto convertDtoToModel(RefertoDTO refertoDTO);

    @Mapping(target = "parametriVitali", ignore = true)
    List<RefertoDTO> convertModelsToDtos(List<Referto> referto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModel(Referto source, @MappingTarget Referto target);

    @Mapping(target = "parametriVitali", ignore = true)
    @Mapping(target = "prenotazione", ignore = true)
    @Mapping(target = "medico", ignore = true)
    @Mapping(target = "paziente", ignore = true)
    @Mapping(target = "isFirmato", constant = "false")
    @Mapping(target = "isInviatoPaziente", constant = "false")
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "allegati", ignore = true)
    @Mapping(target = "firmaDigitale", ignore = true)
    @Mapping(target = "dataFirma", ignore = true)
    @Mapping(target = "dataInvioPaziente", ignore = true)
    @Mapping(target = "dataEmissione", ignore = true)
    Referto convertCreateDTOToModel(RefertoCreateDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "parametriVitali", ignore = true)
    @Mapping(target = "prenotazione", ignore = true)
    @Mapping(target = "medico", ignore = true)
    @Mapping(target = "paziente", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "allegati", ignore = true)
    @Mapping(target = "isFirmato", ignore = true)
    @Mapping(target = "isInviatoPaziente", ignore = true)
    @Mapping(target = "firmaDigitale", ignore = true)
    @Mapping(target = "dataFirma", ignore = true)
    @Mapping(target = "dataInvioPaziente", ignore = true)
    @Mapping(target = "dataEmissione", ignore = true)
    void applyUpdateDTO(RefertoUpdateDTO dto, @MappingTarget Referto referto);

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