package com.development.spring.hGate.H_Gate.repositories;

import com.development.spring.hGate.H_Gate.entity.Prenotazione;
import com.development.spring.hGate.H_Gate.enums.StatoPrenotazioneEnum;
import com.development.spring.hGate.H_Gate.libs.data.repositories.CrudRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PrenotazioneRepository extends CrudRepository<Prenotazione, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Prenotazione p WHERE p.medico.id = :medicoId AND p.stato IN :stati AND ((p.dataOra < :dataOraFine AND p.dataOraFine > :dataOra))")
    List<Prenotazione> findConflittingAppointments(
            @Param("medicoId") Integer medicoId,
            @Param("dataOra")LocalDateTime dataOra,
            @Param("dataOraFine") LocalDateTime dataOraFine,
            @Param("stati") List<String> stati
            );

    /**
     * Verifica se il paziente ha già una prenotazione in un range di date
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM Prenotazione p " +
            "WHERE p.paziente.id = :pazienteId " +
            "AND p.dataOra BETWEEN :start AND :end " +
            "AND p.stato IN ('IN_ATTESA', 'CONFERMATA')")
    boolean existsByPazienteIdAndDataOraBetween(
            @Param("pazienteId") Integer pazienteId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    /**
     * Trova prenotazioni del paziente
     */
    List<Prenotazione> findByPazienteIdOrderByDataOraDesc(Long pazienteId);

    /**
     * Trova prenotazioni del medico per un giorno specifico
     */
    @Query("SELECT p FROM Prenotazione p " +
            "WHERE p.medico.id = :medicoId " +
            "AND p.dataOra BETWEEN :start AND :end " +
            "ORDER BY p.dataOra ASC")
    List<Prenotazione> findByMedicoIdAndDate(
            @Param("medicoId") Long medicoId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    Integer countByDataOraBetween(LocalDateTime start, LocalDateTime end);
    @Query("SELECT COALESCE(SUM(p.costo), 0) FROM Prenotazione p WHERE p.stato = :stato AND p.dataOra BETWEEN :start AND :end")
    BigDecimal sumCostoByStatoAndDataOraBetween(@Param("stato") StatoPrenotazioneEnum stato, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = "SELECT COUNT(*) FROM prenotazioni " +
            "WHERE DAYOFWEEK(data_ora) = :dayOfWeek " +
            "AND data_ora >= DATE_SUB(NOW(), INTERVAL 7 DAY)",
            nativeQuery = true)
    Integer countByDayOfWeek(@Param("dayOfWeek") Integer dayOfWeek);
}
