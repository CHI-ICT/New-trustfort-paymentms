package com.chh.trustfort.payment.repository;

import com.chh.trustfort.payment.model.SystemParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemParameterRepository extends JpaRepository<SystemParameter, Long> {
    Optional<SystemParameter> findByKey(String key);
}
