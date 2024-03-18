package com.maurice.DocumentManagement.repository;

import com.maurice.DocumentManagement.entities.UserEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("SELECT ue FROM UserEntity ue WHERE ue.plan.name = :planName")
    Page<UserEntity> findUsersByPlanName(@Param("planName") String name, Pageable pageable);

    @Query("SELECT sh.users FROM Share sh WHERE sh.id = :shareId")
    List<UserEntity> findUsersByShareId(@Param("shareId") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity ue SET ue.name = :name , ue.lastname = :lastname , ue.email = :email, ue.lastModifiedAt = :lastModifiedAt WHERE ue.id = :userId")
    int updateUser(@Param("name") String name, @Param("lastname") String lastname, @Param("email") String email, @Param("lastModifiedAt") LocalDateTime lastModifiedAt, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity ue SET ue.password = :password, ue.lastModifiedAt = :lastModifiedAt WHERE ue.id = :userId")
    int changePassword(@Param("password") String password, @Param("lastModifiedAt") LocalDateTime lastModifiedAt, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity ue SET ue.plan = (SELECT pl FROM Plan pl WHERE pl.id = :planId), ue.lastModifiedAt = :lastModifiedAt WHERE ue.id = :userId")
    int changePlan(@Param("planId") Long planId, @Param("lastModifiedAt") LocalDateTime lastModifiedAt, @Param("userId") Long userId);

    Page<UserEntity> findAll(Pageable pageable);

    @Query("SELECT ue FROM UserEntity ue WHERE ue.email = :email")
    Optional<UserEntity> findUserByEmail(@Param("email") String email);

    @Query("SELECT ue FROM UserEntity ue WHERE ue.email IN (:emails)")
    List<UserEntity> findUsersBySomeEmails(@Param("emails") List<String> emails);


}
