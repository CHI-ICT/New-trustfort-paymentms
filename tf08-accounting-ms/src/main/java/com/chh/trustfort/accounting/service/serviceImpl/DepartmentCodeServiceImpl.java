package com.chh.trustfort.accounting.service.serviceImpl;

import com.chh.trustfort.accounting.model.DepartmentCode;
import com.chh.trustfort.accounting.payload.DepartmentCodeRequestDTO;
import com.chh.trustfort.accounting.repository.DepartmentCodeRepository;
import com.chh.trustfort.accounting.service.DepartmentCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentCodeServiceImpl implements DepartmentCodeService {

    private final DepartmentCodeRepository repo;

    @Override
    public DepartmentCode create(DepartmentCodeRequestDTO dto) {
        String code = generateNextCode();
        DepartmentCode dept = new DepartmentCode();
        dept.setCode(code);
        dept.setName(dto.getName());
        return repo.save(dept);
    }

    @Override
    public DepartmentCode update(String code, DepartmentCodeRequestDTO dto) {
        DepartmentCode existing = repo.findByCode(code).orElseThrow(() -> new IllegalArgumentException("Code not found"));
        existing.setName(dto.getName());
        return repo.save(existing);
    }

    @Override
    public List<DepartmentCode> findAll() {
        return repo.findAllByIsDeletedFalse();
    }

    @Override
    public Optional<DepartmentCode> findByCode(String code) {
        return repo.findByCodeAndIsDeletedFalse(code);
    }

    @Override
    public String delete(String code) {
        DepartmentCode dept = repo.findByCode(code).orElseThrow(() -> new IllegalArgumentException("Code not found"));
        dept.setDeleted(true);
        repo.save(dept);
        return "Deleted successfully";
    }

    private String generateNextCode() {
        Integer max = repo.findMaxCodeValue();
        int next = (max != null) ? max + 1 : 1;
        if (next > 999) throw new RuntimeException("Maximum department code limit (999) reached.");
        return String.format("%03d", next);
    }

    @Override
    public List<DepartmentCode> findAllDeleted() {
        return repo.findAllByIsDeletedTrue();
    }

    @Override
    public DepartmentCode restore(String code) {
        DepartmentCode dept = repo.findByCode(code).orElseThrow(() -> new IllegalArgumentException("Code not found"));
        dept.setDeleted(false);
        return repo.save(dept);
    }
}

