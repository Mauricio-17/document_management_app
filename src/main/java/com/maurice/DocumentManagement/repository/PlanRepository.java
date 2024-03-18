package com.maurice.DocumentManagement.repository;

import com.maurice.DocumentManagement.entities.Plan;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Plan pl SET pl.name = :name , pl.description = :description , pl.monthlyCost = :monthlyCost WHERE pl.id = :planId")
    int updatePlan(@Param("name") String name, @Param("description") String description, @Param("monthlyCost") Float monthlyCost, @Param("planId") Long planId);

    @Query("SELECT pl FROM Plan pl WHERE pl.name = :name")
    Optional<Plan> findPlanByName(@Param("name") String name);
}
