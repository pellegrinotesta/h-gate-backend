package com.development.spring.hGate.H_Gate.repositories;

import com.development.spring.hGate.H_Gate.entity.PercorsiTerapeutici;
import com.development.spring.hGate.H_Gate.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PercorsiTerapeuticiRepository extends CrudRepository<PercorsiTerapeutici, Integer> {

    List<PercorsiTerapeutici> findByPazienteIdOrderByDataInizioDesc(Integer pazienteId);

    @Query("SELECT p FROM PercorsiTerapeutici p WHERE p.medico.id = :medicoId AND p.paziente.id = :pazienteId")
    List<PercorsiTerapeutici> percorsiTerapeuticiPazienteAndMedico(@Param("medicoId") Integer medicoId, @Param("pazienteId") Integer pazienteId);

}