package com.development.spring.hGate.H_Gate.repositories;

import com.development.spring.hGate.H_Gate.entity.Paziente;
import com.development.spring.hGate.H_Gate.entity.PazienteTutore;
import com.development.spring.hGate.H_Gate.entity.identifies.PazienteTutoreId;
import com.development.spring.hGate.H_Gate.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PazienteTutoreRepository extends CrudRepository<PazienteTutore, PazienteTutoreId> {

    Optional<PazienteTutore> findFirstByPazienteId(Integer pazienteId);
    // Verifica se esiste una relazione tra paziente e tutore
    @Query("SELECT CASE WHEN COUNT(pt) > 0 THEN true ELSE false END " +
            "FROM PazienteTutore pt " +
            "WHERE pt.paziente.id = :pazienteId " +
            "AND pt.tutore.id = :tutoreId")
    boolean existsByPazienteIdAndTutoreId(
            @Param("pazienteId") Integer pazienteId,
            @Param("tutoreId") Integer tutoreId
    );

    @Query("SELECT pt.paziente.id FROM PazienteTutore pt WHERE pt.tutore.user.id = :tutoreUserId")
    List<Integer> findPazienteIdsByTutoreUserId(@Param("tutoreUserId") Integer tutoreUserId);
}
