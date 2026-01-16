package com.development.spring.hGate.H_Gate.repositories;

import com.development.spring.hGate.H_Gate.entity.Paziente;
import com.development.spring.hGate.H_Gate.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PazienteRepository extends CrudRepository<Paziente, Integer> {

    @Query("SELECT p FROM Paziente p JOIN PazienteTutore pt ON pt.paziente.id = p.id JOIN Users u ON u.id = pt.tutore.user.id  WHERE pt.tutore.user.id = :userId")
    Paziente findByUserId(Integer userId);

    boolean existsByCodiceFiscale(String codiceFiscale);

    Optional<Paziente> findByCodiceFiscale(String codiceFiscale);

//    @Query("SELECT COUNT(p) FROM Paziente p WHERE p.user.isActive = :isActive")
//    Integer countByIsActive(@Param("isActive") Boolean isActive);
}
