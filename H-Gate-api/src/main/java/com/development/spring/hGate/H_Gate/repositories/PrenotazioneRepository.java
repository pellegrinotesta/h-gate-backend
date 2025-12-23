package com.development.spring.hGate.H_Gate.repositories;

import com.development.spring.hGate.H_Gate.entity.Prenotazione;
import com.development.spring.hGate.H_Gate.enums.StatoPrenotazioneEnum;
import com.development.spring.hGate.H_Gate.libs.data.repositories.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface PrenotazioneRepository extends CrudRepository<Prenotazione, Integer> {

    Integer countByDataOraBetween(LocalDateTime start, LocalDateTime end);
    @Query("SELECT COALESCE(SUM(p.costo), 0) FROM Prenotazione p WHERE p.stato = :stato AND p.dataOra BETWEEN :start AND :end")
    BigDecimal sumCostoByStatoAndDataOraBetween(@Param("stato") StatoPrenotazioneEnum stato, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query(value = "SELECT COUNT(*) FROM prenotazioni " +
            "WHERE DAYOFWEEK(data_ora) = :dayOfWeek " +
            "AND data_ora >= DATE_SUB(NOW(), INTERVAL 7 DAY)",
            nativeQuery = true)
    Integer countByDayOfWeek(@Param("dayOfWeek") Integer dayOfWeek);
}
