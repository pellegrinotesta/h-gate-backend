package com.development.spring.hGate.H_Gate.repositories;

import com.development.spring.hGate.H_Gate.entity.Allegato;
import com.development.spring.hGate.H_Gate.libs.data.repositories.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllegatoRepository extends CrudRepository<Allegato, Integer> {

    List<Allegato> findByPrenotazioneId(Integer prenotazioneId);

    List<Allegato> findByPrenotazionePazienteId(Integer pazienteId);
    List<Allegato> findByPrenotazionePazienteIdAndPrenotazioneMedicoId(
            Integer pazienteId, Integer medicoId
    );

}
