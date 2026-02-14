package com.development.spring.hGate.H_Gate.controllers;

import com.development.spring.hGate.H_Gate.dtos.ResponseDTO;
import com.development.spring.hGate.H_Gate.dtos.medici.DisponibilitaMediciDTO;
import com.development.spring.hGate.H_Gate.mappers.DisponibilitaMediciIMapper;
import com.development.spring.hGate.H_Gate.security.models.JwtAuthentication;
import com.development.spring.hGate.H_Gate.services.DisponibilitaMediciService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("disponibilita/medico")
public class DisponibilitaMedicoController {

    private final DisponibilitaMediciService disponibilitaMediciService;
    private final DisponibilitaMediciIMapper disponibilitaMediciIMapper;

    @GetMapping("/{medicoId}")
    public ResponseDTO<List<DisponibilitaMediciDTO>> getDisponibilita(@PathVariable("medicoId") Integer medicoId) {
        ResponseDTO<List<DisponibilitaMediciDTO>> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(disponibilitaMediciIMapper.convertModelsToDtos(disponibilitaMediciService.getDisponibilitaMedico(medicoId)));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }

        return res;
    }

    @PostMapping
    public ResponseDTO<DisponibilitaMediciDTO> saveDisponibilita(@RequestBody DisponibilitaMediciDTO dto, JwtAuthentication authentication) {
        ResponseDTO<DisponibilitaMediciDTO> res = new ResponseDTO<>();

        try {
            res.setOk(true);
            res.setData(disponibilitaMediciIMapper.convertModelToDTO(disponibilitaMediciService.salvaDisponibilita(authentication.getId(), dto)));

        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }
        return res;
    }

    @PutMapping("/{giornoSettimana}/disabilita")
    public ResponseDTO<String> disabilitaGiorno(@PathVariable Integer giornoSettimana, JwtAuthentication authentication) {
        ResponseDTO<String> res = new ResponseDTO<>();

        try {
            res.setOk(true);
            res.setData(disponibilitaMediciService.disabilitaGiorno(authentication.getId(), giornoSettimana));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }

        return res;
    }

    @PutMapping("/{giornoSettimana}/abilitaGiorno")
    public ResponseDTO<String> abilitaGiorno(@PathVariable Integer giornoSettimana, JwtAuthentication authentication) {
        ResponseDTO<String> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(disponibilitaMediciService.abilitaGiorno(authentication.getId(), giornoSettimana));

        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }

        return res;
    }

    @DeleteMapping("/{giornoSettimana}")
    public ResponseDTO<String> eliminaDisponibilita(@PathVariable Integer giornoSettimana, JwtAuthentication authentication) {
        ResponseDTO<String> res = new ResponseDTO<>();
        try {

            res.setOk(true);
            res.setData(disponibilitaMediciService.eliminaDisponibilita(authentication.getId(), giornoSettimana));
        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }

        return res;
    }

    @PostMapping("/standard")
    public ResponseDTO<List<DisponibilitaMediciDTO>> configuraStandard(JwtAuthentication authentication) {
        ResponseDTO<List<DisponibilitaMediciDTO>> res = new ResponseDTO<>();
        try {
            res.setOk(true);
            res.setData(disponibilitaMediciIMapper.convertModelsToDtos(disponibilitaMediciService.configuraDisponibilitaStandard(authentication.getId())));

        } catch (Exception ex) {
            res.setOk(false);
            res.setMessage(ex.getMessage());
        }

        return res;
    }
//    @GetMapping("/{medicoUserId}")
//    public ResponseDTO<List<DisponibilitaMediciDTO>> findByMedicoUserId(@PathVariable Integer medicoUserId) {
//        ResponseDTO<List<DisponibilitaMediciDTO>> res = new ResponseDTO<>();
//
//        try {
//            res.setOk(true);
//            res.setData(disponibilitaMediciIMapper.convertModelsToDtos(disponibilitaMediciService.findByMedicoUserId(medicoUserId)));
//        } catch (Exception ex) {
//            res.setOk(false);
//            res.setMessage(ex.getMessage());
//        }
//
//        return res;
//    }
}
