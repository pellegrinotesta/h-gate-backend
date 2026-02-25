package com.development.spring.hGate.H_Gate.mappers;

import com.development.spring.hGate.H_Gate.dtos.medici.MedicoDTO;
import com.development.spring.hGate.H_Gate.dtos.prenotazioni.AllegatoDTO;
import com.development.spring.hGate.H_Gate.entity.Allegato;
import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.libs.web.dtos.PageDTO;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface AllegatoMapper {
    @Mapping(target = "prenotazione.referto.parametriVitali", ignore = true)
    AllegatoDTO convertModelToDTO(Allegato allegato);
    @Mapping(target = "prenotazione.referto.parametriVitali", ignore = true)
    Allegato convertDtoToModel(AllegatoDTO allegatoDTO);
    @Mapping(target = "prenotazione.referto.parametriVitali", ignore = true)
    List<AllegatoDTO> convertModelsToDtos(List<Allegato> allegatoes);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModel(Allegato source, @MappingTarget Allegato target);
    @Mapping(target = "prenotazione.referto.parametriVitali", ignore = true)
    default PageDTO<AllegatoDTO> convertModelsPageToDtosPage(Page<Allegato> modelsPage) {
        return PageDTO.<AllegatoDTO>builder()
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
