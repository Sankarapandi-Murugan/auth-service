package com.auth.authservice.repository;

import com.auth.authservice.model.App;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRepository extends JpaRepository<App, Long> {
    App findByApiKey(String apiKey);
}
