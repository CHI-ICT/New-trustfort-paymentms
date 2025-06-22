package com.chh.trustfort.payment.repository;

import com.chh.trustfort.payment.model.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    Optional<OtpToken> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}
