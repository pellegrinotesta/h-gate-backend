package com.development.spring.hGate.H_Gate.repositories;

import com.development.spring.hGate.H_Gate.entity.Referto;
import com.development.spring.hGate.H_Gate.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefertoRepository extends CrudRepository<Referto, Integer> {

    @Query("""
    SELECT r
    FROM Referto r
    JOIN PazienteTutore pt ON pt.paziente.id = r.paziente.id
    JOIN TutoreLegale t ON pt.tutore.id = t.id
    WHERE t.user.id = :userId
    """)
    List<Referto> listaRefertiPaziente(@Param("userId") Integer userId);

    @Query("SELECT COUNT(DISTINCT m.user.id) FROM Referto r JOIN Medico m ON r.medico.id = m.id WHERE m.user.id = :userId and r.isFirmato is false")
    Integer countByMedicoAndIsFirmato(@Param("userId") Integer userId);

    @Query("SELECT COUNT(r) FROM Referto r " +
            "WHERE r.medico.user.id = :medicoUserId " +
            "AND r.isFirmato = false")
    Integer countByMedicoUserIdAndIsFirmatoFalse(@Param("medicoUserId") Integer medicoUserId);

    // Lista referti di un paziente specifico
    List<Referto> findByPazienteIdOrderByDataEmissioneDesc(Long pazienteId);

    // Lista referti per tutore (tutti i minori del tutore)
    @Query("SELECT r FROM Referto r " +
            "INNER JOIN Paziente p ON r.paziente.id = p.id " +
            "INNER JOIN PazienteTutore pt ON p.id = pt.paziente.id " +
            "INNER JOIN TutoreLegale tl ON pt.tutore.id = tl.id " +
            "WHERE tl.user.id = :tutoreUserId " +
            "ORDER BY r.dataEmissione DESC")
    List<Referto> findByTutoreUserId(@Param("tutoreUserId") Long tutoreUserId);

    // Top 5 referti recenti per tutore
    @Query("SELECT r FROM Referto r " +
            "INNER JOIN Paziente p ON r.paziente.id = p.id " +
            "INNER JOIN PazienteTutore pt ON p.id = pt.paziente.id " +
            "INNER JOIN TutoreLegale tl ON pt.tutore.id = tl.id " +
            "WHERE tl.user.id = :tutoreUserId " +
            "ORDER BY r.dataEmissione DESC " +
            "LIMIT 5")
    List<Referto> findTop5ByTutoreUserIdOrderByDataEmissioneDesc(@Param("tutoreUserId") Integer tutoreUserId);
}
