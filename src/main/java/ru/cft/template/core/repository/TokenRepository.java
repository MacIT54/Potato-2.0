package ru.cft.template.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.cft.template.core.entity.BannedToken;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<BannedToken, String> {
    Optional<BannedToken> findByToken(String token);
}