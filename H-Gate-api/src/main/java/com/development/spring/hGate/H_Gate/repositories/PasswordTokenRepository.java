package com.development.spring.hGate.H_Gate.repositories;

import com.development.spring.hGate.H_Gate.entity.PasswordResetToken;
import com.development.spring.hGate.H_Gate.libs.data.repositories.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PasswordTokenRepository extends CrudRepository<PasswordResetToken, Long> {

    List<PasswordResetToken> findByExpiryDateBefore(Date now);
    PasswordResetToken findByToken(String Token);
}
