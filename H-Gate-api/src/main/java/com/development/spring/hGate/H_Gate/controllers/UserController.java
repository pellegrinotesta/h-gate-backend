package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.UserDTO;
import com.development.spring.hGate.H_Gate.mappers.UserMapper;
import com.development.spring.hGate.H_Gate.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;


    @GetMapping("/{id}")
    public ResponseDTO<UserDTO> getById(@PathVariable("id") Integer id) {
        ResponseDTO<UserDTO> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(userMapper.convertModelToDTO(userService.getById(id)));

        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }

        return res;
    }

}
