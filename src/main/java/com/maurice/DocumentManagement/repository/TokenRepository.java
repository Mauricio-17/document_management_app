package com.maurice.DocumentManagement.repository;

import com.maurice.DocumentManagement.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("SELECT tk FROM Token tk WHERE tk.token =:token")
    Optional<Token> findByToken(@Param("token") String token);

    @Query("SELECT tk FROM Token tk WHERE tk.user.id = :userId")
    List<Token> findTokensByUser(@Param("userId") Long userId);
}
