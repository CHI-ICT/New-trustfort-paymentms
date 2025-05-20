package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.model.AccountCategory;
import com.chh.trustfort.accounting.payload.AccountCategoryRequest;
import com.chh.trustfort.accounting.repository.AccountCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountCategoryService {

    @Autowired
    private AccountCategoryRepository repository;

    public AccountCategory createCategory(AccountCategoryRequest req) {
        if (repository.findByName(req.getName()).isPresent()) {
            throw new IllegalArgumentException("Category already exists");
        }

        return repository.save(AccountCategory.builder()
                .name(req.getName())
                .minCode(req.getMinCode())
                .maxCode(req.getMaxCode())
                .description(req.getDescription())
                .build());
    }
}

