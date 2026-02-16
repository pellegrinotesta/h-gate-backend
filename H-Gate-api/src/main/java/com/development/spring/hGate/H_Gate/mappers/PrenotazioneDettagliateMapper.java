package com.development.spring.hGate.H_Gate.mappers;

import com.development.spring.hGate.H_Gate.dtos.prenotazioni.PrenotazioniDettagliateDTO;
import com.development.spring.hGate.H_Gate.entity.VPrenotazioniDettagliate;
import com.development.spring.hGate.H_Gate.libs.web.dtos.PageDTO;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface PrenotazioneDettagliateMapper {

    PrenotazioniDettagliateDTO convertModelToDTO(VPrenotazioniDettagliate prenotazioniDettagliate);

    VPrenotazioniDettagliate convertDtoToModel(PrenotazioniDettagliateDTO prenotazioniDettagliateDTO);

    List<PrenotazioniDettagliateDTO> convertModelsToDtos(List<VPrenotazioniDettagliate> prenotazioniDettagliates);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModel(VPrenotazioniDettagliate source, @MappingTarget VPrenotazioniDettagliate target);

    default PageDTO<PrenotazioniDettagliateDTO> convertModelsPageToDtosPage(Page<VPrenotazioniDettagliate> modelsPage) {
        return PageDTO.<PrenotazioniDettagliateDTO>builder()
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
