package com.development.spring.hGate.H_Gate.mappers;

import com.development.spring.hGate.H_Gate.dtos.UserDTO;
import com.development.spring.hGate.H_Gate.entity.Users;
import com.development.spring.hGate.H_Gate.libs.web.dtos.PageDTO;
import com.development.spring.hGate.H_Gate.profile.dtos.PatchProfileDTO;
import org.mapstruct.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true))
public interface UserMapper {

    UserDTO convertModelToDTO(Users user);

    Users convertDtoToModel(UserDTO userDTO);

    List<UserDTO> convertModelsToDtos(List<Users> users);

    Users convertProfileDTOtoUser(PatchProfileDTO profileDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModel(Users source, @MappingTarget Users target);

    default PageDTO<UserDTO> convertModelsPageToDtosPage(Page<Users> modelsPage) {
        return PageDTO.<UserDTO>builder()
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
