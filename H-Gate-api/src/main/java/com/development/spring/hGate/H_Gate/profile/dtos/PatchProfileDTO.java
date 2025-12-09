package com.development.spring.hGate.H_Gate.profile.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatchProfileDTO {

    private String email;

    private String name;

    private String surname;

    private String password;

    private String newPassword;

}
