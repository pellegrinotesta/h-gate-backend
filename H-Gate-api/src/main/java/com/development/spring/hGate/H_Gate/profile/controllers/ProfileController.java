package com.development.spring.hGate.H_Gate.profile.controllers;

import com.development.spring.hGate.H_Gate.dtos.UserDTO;
import com.development.spring.hGate.H_Gate.entity.Users;
import com.development.spring.hGate.H_Gate.mappers.UserMapper;
import com.development.spring.hGate.H_Gate.profile.dtos.PatchProfileDTO;
import com.development.spring.hGate.H_Gate.profile.service.ProfileService;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("profile")
public class ProfileController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProfileService profileService;


    @GetMapping
    public UserDTO getProfile(JwtAuthentication jwtAuthentication) {
        return userMapper.convertModelToDTO(profileService.getProfile(jwtAuthentication));
    }

    @PatchMapping
    public UserDTO partialUpdate(JwtAuthentication jwtAuthentication, @RequestBody PatchProfileDTO patchProfileDTO) {
        Users user = userMapper.convertProfileDTOtoUser(patchProfileDTO);
        return userMapper.convertModelToDTO(
                profileService.partialUpdate(jwtAuthentication, user, patchProfileDTO.getNewPassword())
        );
    }

}
