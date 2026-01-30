package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.dashboard.DashboardAdminResponse;
import com.development.spring.hGate.H_Gate.dtos.dashboard.DashboardMedicoResponse;
import com.development.spring.hGate.H_Gate.dtos.dashboard.DashboardPazienteResponse;
import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("dashboard")
public class DashboardController {

    private final DashboardService dashboardService;


    @GetMapping("/paziente")
    @PreAuthorize("hasAuthority('PAZIENTE', 'TUTORE')")
    public ResponseDTO<DashboardPazienteResponse> dashboardPaziente(JwtAuthentication jwtAuthentication) {
        ResponseDTO<DashboardPazienteResponse> res = new ResponseDTO<>();
        try{
            res.setOk(true);
            res.setData(dashboardService.dashboardPaziente(jwtAuthentication.getId()));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @GetMapping("/medico")
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<DashboardMedicoResponse> dashboardMedico(JwtAuthentication jwtAuthentication) {
        ResponseDTO<DashboardMedicoResponse> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(dashboardService.dashboardMedico(jwtAuthentication.getId()));

        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }

        return res;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseDTO<DashboardAdminResponse> dashboardAdmin(JwtAuthentication jwtAuthentication) {
        ResponseDTO<DashboardAdminResponse> res = new ResponseDTO<>();
        try {

            res.setOk(true);
            res.setData(dashboardService.dashboardAdmin(jwtAuthentication.getId()));

        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }

        return res;
    }
}
