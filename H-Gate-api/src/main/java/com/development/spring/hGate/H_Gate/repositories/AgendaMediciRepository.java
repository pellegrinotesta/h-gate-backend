package com.development.spring.hGate.H_Gate.repositories;

import com.development.spring.hGate.H_Gate.entity.VAgendaMedici;
import com.development.spring.hGate.H_Gate.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgendaMediciRepository extends CrudRepository<VAgendaMedici, Integer> {

    @Query("SELECT v FROM VAgendaMedici v WHERE v.medicoId = :medicoId")
    List<VAgendaMedici> findByMedicoId(@Param("medicoId") Integer medicoId);
}
