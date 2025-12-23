package com.development.spring.hGate.H_Gate.services;

import com.development.spring.hGate.H_Gate.entity.Referto;
import com.development.spring.hGate.H_Gate.repositories.RefertoRepository;
import com.development.spring.hGate.H_Gate.shared.entities.BasicEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RefertoService extends BasicEntity {

    private final RefertoRepository refertoRepository;


    public List<Referto> listaRefertiPaziente(Integer userId) {
        return refertoRepository.listaRefertiPaziente(userId);
    }
}
