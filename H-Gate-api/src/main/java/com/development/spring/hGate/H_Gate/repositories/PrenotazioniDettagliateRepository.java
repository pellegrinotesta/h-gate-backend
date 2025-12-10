package com.development.spring.hGate.H_Gate.repositories;

import com.development.spring.hGate.H_Gate.entity.VPrenotazioniDettagliate;
import com.development.spring.hGate.H_Gate.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrenotazioniDettagliateRepository extends CrudRepository<VPrenotazioniDettagliate, Integer> {

    @Query("SELECT p FROM VPrenotazioniDettagliate p WHERE p.pazienteUserId = :pazienteUserId AND p.dataOra >= CURRENT_TIMESTAMP ORDER BY p.dataOra ASC")
    List<VPrenotazioniDettagliate> prenotazioniPaziente(Integer pazienteUserId);

}
