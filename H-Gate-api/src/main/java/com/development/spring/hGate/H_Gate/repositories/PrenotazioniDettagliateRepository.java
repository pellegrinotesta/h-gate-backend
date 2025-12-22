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

    // Query Pazienti
     Integer countByPazienteUserIdAndDataOraAfterAndStatoIn(Integer pazienteUserId, Date dataOra, List<String> stati);

     Integer countByPazienteUserId(Integer pazienteUserId);

//    @Query("SELECT COUNT(DISTINCT p.medico.id) FROM Prenotazione p WHERE p.paziente.user.id = :userId")
//    Integer countDistinctMediciByPaziente(@Param("userId") Integer userId);

    List<VPrenotazioniDettagliate> findTop5ByPazienteUserIdAndDataOraAfterAndStatoInOrderByDataOraAsc(Integer pazienteUserId, Date dataOra,  List<String> stati);

    // QUERY MEDICI
    Integer countByMedicoUserIdAndDataOraBetweenAndStatoIn(Integer medicoUserId, LocalDateTime start, LocalDateTime end, List<String> stati);
    @Query("SELECT COUNT(DISTINCT p.medicoUserId) FROM VPrenotazioniDettagliate p WHERE p.medicoUserId = :medicoUserId")
    Integer countDistinctPazientiByMedico(@Param("medicoUserId") Integer medicoUserId);
    List<VPrenotazioniDettagliate> findByMedicoUserIdAndDataOraBetweenAndStatoInOrderByDataOraAsc(Integer medicoUserId, LocalDateTime start, LocalDateTime end, List<String> stati);
    Integer countByMedicoUserIdAndStato(Integer medicoUserId, String stato);
}
