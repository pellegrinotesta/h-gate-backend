package com.development.spring.hGate.H_Gate.repositories;

import com.development.spring.hGate.H_Gate.entity.Notifica;
import com.development.spring.hGate.H_Gate.enums.TipoNotificaEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificheRepository extends CrudRepository<Notifica, Integer> {
    /**
     * Trova tutte le notifiche di un utente (con paginazione)
     */
    Page<Notifica> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);

    /**
     * Trova notifiche non lette di un utente
     */
    List<Notifica> findByUserIdAndIsLettaFalseOrderByCreatedAtDesc(Integer userId);

    /**
     * Conta notifiche non lette
     */
    Long countByUserIdAndIsLettaFalse(Integer userId);

//    List<Notifica> findByPrenotazioneIdAndIsLettaFalse(Integer prenotazioneId);

    /**
     * Marca tutte le notifiche come lette
     */
    @Modifying
    @Query("UPDATE Notifica n SET n.isLetta = true, n.dataLettura = :dataLettura " +
            "WHERE n.user.id = :userId AND n.isLetta = false")
    void marcaTutteComeLette(
            @Param("userId") Integer userId,
            @Param("dataLettura") LocalDateTime dataLettura
    );

    /**
     * Elimina notifiche vecchie (pulizia automatica)
     */
    @Modifying
    @Query("DELETE FROM Notifica n WHERE n.createdAt < :data AND n.isLetta = true")
    void eliminaNotificheVecchie(@Param("data") LocalDateTime data);

    /**
     * Trova notifiche non inviate via email (per retry)
     */
    @Query("SELECT n FROM Notifica n WHERE n.isInviataEmail = false AND n.createdAt > :dataMinima")
    List<Notifica> findNotificheNonInviate(@Param("dataMinima") LocalDateTime dataMinima);
}
