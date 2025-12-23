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
    Integer countByIsDisponibile(Boolean isDisponibile);
    List<Medico> findByIsVerificatoFalse();
    @Query("SELECT m.specializzazione, COUNT(p) as cnt FROM Prenotazione p JOIN p.medico m GROUP BY m.specializzazione ORDER BY cnt DESC")
    List<Object[]> findTopSpecializzazioni();
    boolean existsByNumeroAlbo(String numeroAlbo);
    Optional<Medico> findByNumeroAlbo(String numeroAlbo);
}
