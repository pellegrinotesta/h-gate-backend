package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.dashboard.DashboardAdminResponse;
import com.development.spring.hGate.H_Gate.dtos.dashboard.DashboardAdminResponse.*;
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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("dashboard")
public class DashboardController {

    private final DashboardService dashboardService;


    @GetMapping("/tutore")
    @PreAuthorize("hasAuthority('TUTORE')")
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

    @GetMapping("/kpi")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseDTO<KpiData> dashboardAdmin() {
        ResponseDTO<KpiData> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(dashboardService.getKpiData());

        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }

        return res;
    }

    @GetMapping
    public ResponseDTO<DashboardAdminResponse> getDashboard() {
       ResponseDTO<DashboardAdminResponse> res = new ResponseDTO<>();
       try {
           res.setOk(true);
           res.setData(dashboardService.getDashboardData());
       } catch (Exception ex) {
           res.setOk(false);
           res.setMessage(ex.getMessage());
       }
        return res;
    }

    @GetMapping("/medici-da-verificare")

    public ResponseDTO<List<MedicoDaVerificare>> getMediciDaVerificare() {

        ResponseDTO<List<MedicoDaVerificare>> res = new ResponseDTO<>();

        try {
            res.setOk(true);
            res.setData(dashboardService.getMediciDaVerificare());
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

}
