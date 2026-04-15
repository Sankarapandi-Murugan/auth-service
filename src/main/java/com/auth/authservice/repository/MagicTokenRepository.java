package com.auth.authservice.repository;

import com.auth.authservice.model.MagicToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MagicTokenRepository extends JpaRepository<MagicToken, Long> {
    MagicToken findByToken(String token);
}
