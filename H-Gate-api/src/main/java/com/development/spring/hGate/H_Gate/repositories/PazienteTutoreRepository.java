package com.development.spring.hGate.H_Gate.repositories;

import com.development.spring.hGate.H_Gate.entity.Paziente;
import com.development.spring.hGate.H_Gate.entity.PazienteTutore;
import com.development.spring.hGate.H_Gate.entity.identifies.PazienteTutoreId;
import com.development.spring.hGate.H_Gate.libs.data.repositories.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PazienteTutoreRepository extends CrudRepository<PazienteTutore, PazienteTutoreId> {

    Optional<PazienteTutore> findFirstByPazienteId(Integer pazienteId);
}
