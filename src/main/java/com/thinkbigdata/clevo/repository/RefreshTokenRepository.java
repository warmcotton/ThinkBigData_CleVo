package com.thinkbigdata.clevo.repository;

import com.thinkbigdata.clevo.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByEmail(String email);

    Optional<RefreshToken> findByValue(String token);
}
