package com.development.spring.hGate.H_Gate.mappers;

import com.development.spring.hGate.H_Gate.dtos.MedicoDTO;
import com.development.spring.hGate.H_Gate.dtos.prenotazioni.PrenotazioneDTO;
import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.entity.Prenotazione;
import com.development.spring.hGate.H_Gate.libs.web.dtos.PageDTO;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface PrenotazioneMapper {

    PrenotazioneDTO convertModelToDTO(Prenotazione prenotazione);

    Prenotazione convertDtoToModel(PrenotazioneDTO prenotazioneDTO);

    List<PrenotazioneDTO> convertModelsToDtos(List<Prenotazione> prenotaziones);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModel(Prenotazione source, @MappingTarget Prenotazione target);

    default PageDTO<PrenotazioneDTO> convertModelsPageToDtosPage(Page<Prenotazione> modelsPage) {
        return PageDTO.<PrenotazioneDTO>builder()
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
