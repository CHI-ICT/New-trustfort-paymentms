package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.model.DepartmentCode;
import com.chh.trustfort.accounting.payload.DepartmentCodeRequestDTO;

import java.util.List;
import java.util.Optional;

public interface DepartmentCodeService {
    DepartmentCode create(DepartmentCodeRequestDTO dto);
    DepartmentCode update(String code, DepartmentCodeRequestDTO dto);
    Optional<DepartmentCode> findByCode(String code);
    List<DepartmentCode> findAll();
    String delete(String code);
    List<DepartmentCode> findAllDeleted();
    DepartmentCode restore(String code);

}

