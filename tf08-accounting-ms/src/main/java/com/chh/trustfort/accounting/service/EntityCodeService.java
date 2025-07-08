package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.component.ResponseCode;
import com.chh.trustfort.accounting.exception.ApiException;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.EntityCode;
import com.chh.trustfort.accounting.payload.EntityCodeRequest;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.repository.EntityCodeRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EntityCodeService {

    private final EntityCodeRepository repository;
    private final AesService aesService;
    private final MessageSource messageSource;
    private final Gson gson;

    public String create(EntityCodeRequest req, AppUser appUser) {
        OmniResponsePayload response = new OmniResponsePayload();
        response.setResponseCode(ResponseCode.FAILED_TRANSACTION.getResponseCode());
        response.setResponseMessage(messageSource.getMessage("failed", null, Locale.ENGLISH));

        if (req == null || req.getCode() == null || req.getSubsidiary() == null) {
            log.warn("❌ Request or required fields are null");
            response.setResponseMessage("Invalid request data.");
            return aesService.encrypt(gson.toJson(response), appUser);
        }

        if (repository.findBySubsidiary(req.getSubsidiary()).isPresent()) {
            log.warn("⚠️ Subsidiary already exists: {}", req.getSubsidiary());
            response.setResponseMessage("Subsidiary already exists.");
            return aesService.encrypt(gson.toJson(response), appUser);
        }

        if (repository.findByCode(req.getCode()).isPresent()) {
            log.warn("⚠️ Code already exists: {}", req.getCode());
            response.setResponseMessage("Code already exists.");
            return aesService.encrypt(gson.toJson(response), appUser);
        }

        EntityCode entity = EntityCode.builder()
                .subsidiary(req.getSubsidiary())
                .code(req.getCode())
                .build();

        EntityCode saved = repository.save(entity);
        log.info("✅ Entity code created successfully: {}", saved.getId());

        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
        response.setResponseMessage("Entity code created successfully");
        response.setData(saved);

        return aesService.encrypt(gson.toJson(response), appUser);
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
