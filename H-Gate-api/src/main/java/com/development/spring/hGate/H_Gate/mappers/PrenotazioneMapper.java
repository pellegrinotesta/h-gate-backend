package com.development.spring.hGate.H_Gate.mappers;

import com.development.spring.hGate.H_Gate.dtos.medici.ParametriVitaliDTO;
import com.development.spring.hGate.H_Gate.dtos.prenotazioni.PrenotazioneDTO;
import com.development.spring.hGate.H_Gate.entity.Prenotazione;
import com.development.spring.hGate.H_Gate.libs.web.dtos.PageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface PrenotazioneMapper {

    @Mapping(target = "referto.parametriVitali", source = "referto.parametriVitali", qualifiedByName = "stringToParametriVitali")
    PrenotazioneDTO convertModelToDTO(Prenotazione prenotazione);

    @Mapping(target = "referto.parametriVitali", source = "referto.parametriVitali", qualifiedByName = "parametriVitaliToString")
    Prenotazione convertDtoToModel(PrenotazioneDTO prenotazioneDTO);

    List<PrenotazioneDTO> convertModelsToDtos(List<Prenotazione> prenotaziones);

    @Named("stringToParametriVitali")
    default ParametriVitaliDTO stringToParametriVitali(String json) {
        if (json == null) return null;
        try {
            return new ObjectMapper().readValue(json, ParametriVitaliDTO.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Named("parametriVitaliToString")
    default String parametriVitaliToString(ParametriVitaliDTO dto) {
        if (dto == null) return null;
        try {
            return new ObjectMapper().writeValueAsString(dto);
        } catch (Exception e) {
            return null;
        }
    }
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModel(Prenotazione source, @MappingTarget Prenotazione target);

    @Mapping(target = "referto.parametriVitali", ignore = true)
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
