package com.development.spring.hGate.H_Gate.repositories;

import com.development.spring.hGate.H_Gate.entity.ValutazionePsicologica;
import com.development.spring.hGate.H_Gate.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ValutazionePsicologicaRepository extends CrudRepository<ValutazionePsicologica, Integer> {


    @Query("SELECT v FROM ValutazionePsicologica v WHERE v.paziente.id = :pazienteId AND v.medico.id = :medicoId")
    List<ValutazionePsicologica> valutazioniPsicologicaPazienteAndMedico(@Param("pazienteId") Integer pazienteId, @Param("medicoId") Integer medicoId);

    List<ValutazionePsicologica> findByPazienteIdOrderByDataValutazioneDesc(Integer pazienteId);
}
