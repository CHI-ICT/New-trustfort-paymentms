package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.model.EntityCode;
import com.chh.trustfort.accounting.payload.EntityCodeRequest;
import com.chh.trustfort.accounting.repository.EntityCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EntityCodeService {

    @Autowired
    private EntityCodeRepository repository;

    public EntityCode create(EntityCodeRequest req) {
        if (repository.findBySubsidiary(req.getSubsidiary()).isPresent()) {
            throw new IllegalArgumentException("Subsidiary already exists");
        }

        if (repository.findByCode(req.getCode()).isPresent()) {
            throw new IllegalArgumentException("Code already exists");
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
                .orElseThrow(() -> new IllegalArgumentException("Entity not found"));

        entity.setSubsidiary(req.getSubsidiary());
        entity.setCode(req.getCode());

        return repository.save(entity);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Entity not found");
        }
        repository.deleteById(id);
    }
}
