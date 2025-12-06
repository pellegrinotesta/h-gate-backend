package com.development.spring.hGate.H_Gate.repositories;

import com.development.spring.hGate.H_Gate.entity.Paziente;
import com.development.spring.hGate.H_Gate.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PazienteRepository extends CrudRepository<Paziente, Integer> {

    @Query("SELECT p FROM Paziente p WHERE p.user.id = :userId")
    Paziente findByUserId(Integer userId);

    boolean existsByCodiceFiscale(String codiceFiscale);

    Optional<Paziente> findByCodiceFiscale(String codiceFiscale);
}
