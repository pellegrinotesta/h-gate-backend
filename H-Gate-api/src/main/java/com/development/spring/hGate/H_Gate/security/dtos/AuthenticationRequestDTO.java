package com.development.spring.hGate.H_Gate.security.dtos;

import lombok.*;


@Setter
@Getter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequestDTO {

    private String username;

    private String password;

}
