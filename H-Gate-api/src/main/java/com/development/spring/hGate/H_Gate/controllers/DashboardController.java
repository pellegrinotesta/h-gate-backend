package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.dashboard.DashboardAdminResponse;
import com.development.spring.hGate.H_Gate.dtos.dashboard.DashboardAdminResponse.*;
import com.development.spring.hGate.H_Gate.dtos.dashboard.DashboardMedicoResponse;
import com.development.spring.hGate.H_Gate.dtos.dashboard.DashboardPazienteResponse;
import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("dashboard")
@Tag(name = "Dashboard", description = "Endpoint per il recupero dei dati statistici e riepilogativi differenziati per ruolo")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(
            summary = "Dashboard Tutore/Paziente",
            description = "Recupera i dati della dashboard relativi ai pazienti associati al tutore autenticato."
    )
    @GetMapping("/tutore")
    @PreAuthorize("hasAuthority('TUTORE')")
    public ResponseDTO<DashboardPazienteResponse> dashboardPaziente(
            @Parameter(hidden = true) JwtAuthentication jwtAuthentication) {
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

    @Operation(
            summary = "Dashboard Medico",
            description = "Recupera i dati statistici e gli appuntamenti del giorno per il medico autenticato."
    )
    @GetMapping("/medico")
    @PreAuthorize("hasAuthority('MEDICO')")
    public ResponseDTO<DashboardMedicoResponse> dashboardMedico(
            @Parameter(hidden = true) JwtAuthentication jwtAuthentication) {
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

    @Operation(
            summary = "KPI Admin",
            description = "Restituisce i Key Performance Indicators (indicatori chiave) per l'amministratore del sistema."
    )
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

    @Operation(
            summary = "Dashboard Admin Generale",
            description = "Recupera i dati completi della dashboard per la gestione amministrativa."
    )
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
}