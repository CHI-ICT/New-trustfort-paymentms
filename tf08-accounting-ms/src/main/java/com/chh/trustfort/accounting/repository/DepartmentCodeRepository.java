package com.chh.trustfort.accounting.repository;

import com.chh.trustfort.accounting.model.DepartmentCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DepartmentCodeRepository extends JpaRepository<DepartmentCode, Long> {
    Optional<DepartmentCode> findByCode(String code);
    boolean existsByCode(String code);

    @Query("SELECT MAX(CAST(dc.code AS int)) FROM DepartmentCode dc")
    Integer findMaxCodeValue();
    List<DepartmentCode> findAllByIsDeletedFalse();
    List<DepartmentCode> findAllByIsDeletedTrue();
    Optional<DepartmentCode> findByCodeAndIsDeletedFalse(String code);
}