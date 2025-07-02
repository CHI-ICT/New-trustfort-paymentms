package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.exception.ApiException;
import com.chh.trustfort.accounting.model.EntityCode;
import com.chh.trustfort.accounting.payload.EntityCodeRequest;
import com.chh.trustfort.accounting.repository.EntityCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntityCodeService {

    private final EntityCodeRepository repository;

    public EntityCode create(EntityCodeRequest req) {
        if (repository.findBySubsidiary(req.getSubsidiary()).isPresent()) {
            throw new ApiException("Subsidiary already exists", HttpStatus.CONFLICT);
        }

        if (repository.findByCode(req.getCode()).isPresent()) {
            throw new ApiException("Code already exists", HttpStatus.CONFLICT);
        }

        EntityCode entity = EntityCode.builder()
                .subsidiary(req.getSubsidiary())
                .code(req.getCode())
                .build();

        return repository.save(entity);
    }

    public List<EntityCode> getAll() {
        return repository.findAll();
    }

    public Optional<EntityCode> getById(Long id) {
        return repository.findById(id);
    }

    public EntityCode update(Long id, EntityCodeRequest req) {
        EntityCode entity = repository.findById(id)
                .orElseThrow(() -> new ApiException("Entity not found", HttpStatus.NOT_FOUND));

        entity.setSubsidiary(req.getSubsidiary());
        entity.setCode(req.getCode());

        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ApiException("Entity not found", HttpStatus.NOT_FOUND);
        }
        repository.deleteById(id);
    }
}
