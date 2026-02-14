package com.development.spring.hGate.H_Gate.repositories;

import com.development.spring.hGate.H_Gate.entity.DisponibilitaMedico;
import com.development.spring.hGate.H_Gate.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DisponibilitaMediciRepository extends CrudRepository<DisponibilitaMedico, Integer> {

    @Query("SELECT d FROM DisponibilitaMedico d WHERE d.medico.user.id = :medicoUserId")
    List<DisponibilitaMedico> findByMedicoUserId(@Param("medicoUserId") Integer medicoUserId);

    @Query("SELECT d FROM DisponibilitaMedico d WHERE d.medico.id = :medicoId")
    List<DisponibilitaMedico> findByMedicoIdOrderByGiornoSettimana(@Param("medicoId") Integer medicoId);

    /**
     * Trova la disponibilità per un medico in uno specifico giorno della settimana
     */
    Optional<DisponibilitaMedico> findByMedicoIdAndGiornoSettimana(
            Integer medicoId,
            Integer giornoSettimana
    );

    /**
     * Trova disponibilità attive per un giorno specifico
     */
    Optional<DisponibilitaMedico> findByMedicoIdAndGiornoSettimanaAndIsAttivaTrue(
            Integer medicoId,
            Integer giornoSettimana
    );

    /**
     * Trova tutte le disponibilità attive di un medico
     */
    List<DisponibilitaMedico> findByMedicoIdAndIsAttivaTrue(Integer medicoId);

    /**
     * Elimina tutte le disponibilità di un medico
     */
    void deleteByMedicoId(Integer medicoId);

    /**
     * Verifica se esiste una disponibilità per un medico in un giorno
     */
    boolean existsByMedicoIdAndGiornoSettimana(Integer medicoId, Integer giornoSettimana);
}
