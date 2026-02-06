package com.development.spring.hGate.H_Gate.repositories;

import com.development.spring.hGate.H_Gate.entity.TariffeMedici;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface TariffeMediciRepository extends CrudRepository<TariffeMedici, Integer> {

    @Query("SELECT t.costo FROM TariffeMedici t where t.medico.id = :medicoId AND t.tipoVisita = :tipoVisita AND t.isPrimaVisita = :isPrimaVisita")
    Optional<BigDecimal> findCosto(Integer medicoId, String tipoVisita, boolean isPrimaVisita);
}
