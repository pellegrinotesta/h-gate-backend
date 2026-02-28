package com.development.spring.hGate.H_Gate.repositories;

import com.development.spring.hGate.H_Gate.entity.Medico;
import com.development.spring.hGate.H_Gate.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicoRepository extends CrudRepository<Medico, Integer> {

    @Query("SELECT m FROM Medico m WHERE m.user.id = :id")
    Medico findMedicoByUserId(Integer id);

    boolean existsByNumeroAlbo(String numeroAlbo);

    @Query("""
        SELECT m FROM Medico m
        JOIN FETCH m.user u
        WHERE m.isVerificato = false
          AND u.isActive = true
    """)
    List<Medico> findMediciDaVerificare();

    Optional<Medico> findById(Integer medicoId);
}
