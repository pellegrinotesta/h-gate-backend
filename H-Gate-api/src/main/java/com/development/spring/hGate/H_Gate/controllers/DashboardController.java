package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.DashboardResponse;
import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("dashboard")
public class DashboardController {

    private final DashboardService dashboardService;


    @GetMapping("/paziente")
    public ResponseDTO<DashboardResponse> dashboardPaziente(JwtAuthentication jwtAuthentication) {
        ResponseDTO<DashboardResponse> res = new ResponseDTO<>();
        try{

            res.setOk(true);
            res.setData(dashboardService.dashboardPaziente(jwtAuthentication.getId()));

        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }
}
