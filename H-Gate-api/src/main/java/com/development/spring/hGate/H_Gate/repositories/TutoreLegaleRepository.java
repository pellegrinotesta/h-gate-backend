package com.development.spring.hGate.H_Gate.repositories;

import com.development.spring.hGate.H_Gate.entity.TutoreLegale;
import com.development.spring.hGate.H_Gate.libs.data.repositories.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TutoreLegaleRepository extends CrudRepository<TutoreLegale, Integer> {

    Optional<TutoreLegale> findByUserId(Integer userId);
}
