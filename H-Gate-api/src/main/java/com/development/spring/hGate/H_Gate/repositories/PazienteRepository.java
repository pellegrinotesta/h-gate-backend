package com.development.spring.hGate.H_Gate.repositories;

import com.development.spring.hGate.H_Gate.entity.Paziente;
import com.development.spring.hGate.H_Gate.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PazienteRepository extends CrudRepository<Paziente, Integer> {

    @Query("SELECT p FROM Paziente p " +
            "JOIN PazienteTutore pt ON pt.paziente.id = p.id " +
            "JOIN TutoreLegale t ON pt.tutore.id = t.id " +
            "WHERE t.user.id = :userId")
    List<Paziente> findByUserId(@Param("userId") Integer userId);

    Optional<Paziente> findById(Integer pazienteId);

    Optional<Paziente> findByCodiceFiscale(String codiceFiscale);

//    @Query("SELECT COUNT(p) FROM Paziente p WHERE p.user.isActive = :isActive")
//    Integer countByIsActive(@Param("isActive") Boolean isActive);
}
