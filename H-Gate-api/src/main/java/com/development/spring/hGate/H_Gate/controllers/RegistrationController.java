package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.UserRegistrationDTO;
import com.development.spring.hGate.H_Gate.entity.Users;
import com.development.spring.hGate.H_Gate.services.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("registration")
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("")
    public ResponseDTO<Users> register(@Valid  @RequestBody UserRegistrationDTO dto) {
        ResponseDTO<Users> res = new ResponseDTO<>();

        try {
            res.setOk(true);
            res.setData(registrationService.registerUser(dto));

        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }

        return res;
    }
}
