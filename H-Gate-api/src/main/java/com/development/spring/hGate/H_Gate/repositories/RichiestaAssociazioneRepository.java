package com.development.spring.hGate.H_Gate.repositories;

import com.development.spring.hGate.H_Gate.entity.RichiestaAssociazione;
import com.development.spring.hGate.H_Gate.enums.StatoRichiestaEnum;
import com.development.spring.hGate.H_Gate.libs.data.repositories.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RichiestaAssociazioneRepository extends CrudRepository<RichiestaAssociazione, Integer> {

    List<RichiestaAssociazione> findByStatoOrderByCreatedAtAsc(StatoRichiestaEnum stato);

    Optional<RichiestaAssociazione> findFirstByPazienteIdAndTutoreIdAndStato(
            Integer pazienteId, Integer tutoreId, StatoRichiestaEnum stato);

    long countByStato(StatoRichiestaEnum stato);
}
