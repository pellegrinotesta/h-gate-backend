package com.development.spring.hGate.H_Gate.repositories;

import com.development.spring.hGate.H_Gate.entity.VPrenotazioniDettagliate;
import com.development.spring.hGate.H_Gate.enums.StatoPrenotazioneEnum;
import com.development.spring.hGate.H_Gate.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface PrenotazioniDettagliateRepository extends CrudRepository<VPrenotazioniDettagliate, Integer> {

    // PAZIENTE - Prossime prenotazioni per tutore (via user_id del tutore)
    @Query("SELECT v FROM VPrenotazioniDettagliate v " +
            "WHERE v.tutoreUserId = :tutoreUserId " +
            "AND v.dataOra > :now " +
            "AND v.stato IN :stati " +
            "ORDER BY v.dataOra ASC")
    List<VPrenotazioniDettagliate> findTop5ByTutoreUserIdAndDataOraAfterAndStatoInOrderByDataOraAsc(
            @Param("tutoreUserId") Integer tutoreUserId,
            @Param("now") LocalDateTime now,
            @Param("stati") List<String> stati
    );

    // PAZIENTE - Conta prossimi appuntamenti per tutore
    @Query("SELECT COUNT(v) FROM VPrenotazioniDettagliate v " +
            "WHERE v.tutoreUserId = :tutoreUserId " +
            "AND v.dataOra > :now " +
            "AND v.stato IN :stati")
    Integer countByTutoreUserIdAndDataOraAfterAndStatoIn(
            @Param("tutoreUserId") Integer tutoreUserId,
            @Param("now") LocalDateTime now,
            @Param("stati") List<String> stati
    );

    // PAZIENTE - Conta visite totali per tutore
    @Query("SELECT COUNT(v) FROM VPrenotazioniDettagliate v " +
            "WHERE v.tutoreUserId = :tutoreUserId")
    Integer countByTutoreUserId(@Param("tutoreUserId") Integer tutoreUserId);

    // MEDICO - Visite oggi
    @Query("SELECT COUNT(v) FROM VPrenotazioniDettagliate v " +
            "WHERE v.medicoUserId = :medicoUserId " +
            "AND v.dataOra BETWEEN :startOfDay AND :endOfDay " +
            "AND v.stato IN :stati")
    Integer countByMedicoUserIdAndDataOraBetweenAndStatoIn(
            @Param("medicoUserId") Integer medicoUserId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            @Param("stati") List<String> stati
    );

    // MEDICO - Pazienti distinti
    @Query("SELECT COUNT(DISTINCT v.pazienteId) FROM VPrenotazioniDettagliate v " +
            "WHERE v.medicoUserId = :medicoUserId")
    Integer countDistinctPazientiByMedico(@Param("medicoUserId") Integer medicoUserId);

    // MEDICO - Prenotazioni completate senza referto
    @Query("SELECT COUNT(v) FROM VPrenotazioniDettagliate v " +
            "WHERE v.medicoUserId = :medicoUserId " +
            "AND v.stato = :stato")
    Integer countByMedicoUserIdAndStato(
            @Param("medicoUserId") Integer medicoUserId,
            @Param("stato") String stato
    );

    // MEDICO - Appuntamenti di oggi
    @Query("SELECT v FROM VPrenotazioniDettagliate v " +
            "WHERE v.medicoUserId = :medicoUserId " +
            "AND v.dataOra BETWEEN :startOfDay AND :endOfDay " +
            "AND v.stato IN :stati " +
            "ORDER BY v.dataOra ASC")
    List<VPrenotazioniDettagliate> findByMedicoUserIdAndDataOraBetweenAndStatoInOrderByDataOraAsc(
            @Param("medicoUserId") Integer medicoUserId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            @Param("stati") List<String> stati
    );
}
