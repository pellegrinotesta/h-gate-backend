package com.development.spring.hGate.H_Gate.repositories;

import com.development.spring.hGate.H_Gate.entity.DisponibilitaMedico;
import com.development.spring.hGate.H_Gate.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisponibilitaMediciRepository extends CrudRepository<DisponibilitaMedico, Integer> {

    @Query("SELECT d FROM DisponibilitaMedico d WHERE d.medico.user.id = :medicoUserId")
    List<DisponibilitaMedico> findByMedicoUserId(@Param("medicoUserId") Integer medicoUserId);
}
