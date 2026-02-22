package com.development.spring.hGate.H_Gate.repositories;

import com.development.spring.hGate.H_Gate.entity.VDashboardKpi;
import com.development.spring.hGate.H_Gate.libs.data.repositories.CrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface DashboardKpiRepository extends CrudRepository<VDashboardKpi, LocalDateTime> {

    @Query(value = "SELECT * FROM v_dashboard_kpi LIMIT 1", nativeQuery = true)
    Optional<VDashboardKpi> findLatestKpi();
}
