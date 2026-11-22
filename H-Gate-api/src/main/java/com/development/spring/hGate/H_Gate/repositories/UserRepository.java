package com.development.spring.hGate.H_Gate.repositories;

import com.development.spring.hGate.H_Gate.entity.Users;
import com.development.spring.hGate.H_Gate.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<Users, Integer> {

    @Transactional
    @Query("SELECT u FROM Users u WHERE u.email = :email")
    Optional<Users> findByEmail(@Param("email") String email);

    @Transactional
    @Query("SELECT u FROM Users u WHERE u.id = :userId")
    Optional<Users> findByUserId(Long userId);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM Users u JOIN u.roles r WHERE r = :role")
    boolean existsByRole(@Param("role") com.development.spring.hGate.H_Gate.shared.models.Role role);

    @Query("SELECT u FROM Users u JOIN u.roles r WHERE r = :role AND (u.isActive IS NULL OR u.isActive = true)")
    java.util.List<Users> findActiveByRole(@Param("role") com.development.spring.hGate.H_Gate.shared.models.Role role);
}
