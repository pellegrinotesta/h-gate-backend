package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.UserDTO;
import com.development.spring.hGate.H_Gate.mappers.UserMapper;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
@Tag(name = "Utente", description = "Endpoint per la gestione del profilo e delle informazioni dell'utente autenticato")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @Operation(
            summary = "Ottieni profilo corrente",
            description = "Restituisce i dettagli dell'utente attualmente autenticato basandosi sul token JWT fornito."
    )
    @GetMapping("/me")
    public ResponseDTO<UserDTO> getById(
            @Parameter(hidden = true) JwtAuthentication jwtAuthentication) {
        ResponseDTO<UserDTO> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(userMapper.convertModelToDTO(userService.getById(jwtAuthentication.getId())));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }
}