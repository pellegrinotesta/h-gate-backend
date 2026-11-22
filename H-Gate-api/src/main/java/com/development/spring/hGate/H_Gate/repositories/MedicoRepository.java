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
        WHERE u.isActive = true
          AND (u.isVerified IS NULL OR u.isVerified = false)
        ORDER BY u.createdAt ASC
    """)
    List<Medico> findMediciDaVerificare();

    /** Solo medici approvati dalla clinica e con account attivo. */
    @Query("""
        SELECT m FROM Medico m
        JOIN FETCH m.user u
        WHERE u.isVerified = true AND (u.isActive IS NULL OR u.isActive = true)
    """)
    List<Medico> findMediciApprovati();

    Optional<Medico> findById(Integer medicoId);
}
