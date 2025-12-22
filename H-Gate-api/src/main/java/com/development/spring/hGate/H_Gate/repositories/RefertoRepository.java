package com.development.spring.hGate.H_Gate.repositories;

import com.development.spring.hGate.H_Gate.entity.Referto;
import com.development.spring.hGate.H_Gate.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefertoRepository extends CrudRepository<Referto, Integer> {

    @Query("SELECT r FROM Referto r JOIN Paziente p ON r.paziente.id = p.id WHERE p.user.id = :userId")
    List<Referto> listaRefertiPaziente(@Param("userId") Integer userId);

    @Query("SELECT COUNT(DISTINCT m.user.id) FROM Referto r JOIN Medico m ON r.medico.id = m.id WHERE m.user.id = :userId and r.isFirmato is false")
    Integer countByMedicoAndIsFirmato(@Param("userId") Integer userId);
}
