package com.development.spring.hGate.H_Gate.repositories;

import com.development.spring.hGate.H_Gate.entity.Consenso;
import com.development.spring.hGate.H_Gate.enums.TipoConsensoEnum;
import com.development.spring.hGate.H_Gate.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsensoRepository extends CrudRepository<Consenso, Integer> {

    Optional<Consenso> findFirstByPazienteIdAndTipoOrderByDataEventoDescIdDesc(Integer pazienteId, TipoConsensoEnum tipo);

    @Query("SELECT c FROM Consenso c LEFT JOIN FETCH c.user WHERE c.paziente.id = :pazienteId ORDER BY c.dataEvento DESC, c.id DESC")
    List<Consenso> storico(@Param("pazienteId") Integer pazienteId);
}
