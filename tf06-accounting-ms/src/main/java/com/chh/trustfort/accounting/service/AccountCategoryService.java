package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.model.AccountCategory;
import com.chh.trustfort.accounting.payload.AccountCategoryRequest;
import com.chh.trustfort.accounting.repository.AccountCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public List<AccountCategory> getAllCategories() {
        return repository.findAll();
    }

    public Optional<AccountCategory> getCategoryById(Long id) {
        return repository.findById(id);
    }

    public AccountCategory updateCategory(Long id, AccountCategoryRequest req) {
        AccountCategory category = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        category.setName(req.getName());
        category.setMinCode(req.getMinCode());
        category.setMaxCode(req.getMaxCode());
        category.setDescription(req.getDescription());

        return repository.save(category);
    }

//    public void deleteCategory(Long id) {
//        if (!repository.existsById(id)) {
//            throw new IllegalArgumentException("Category not found");
//        }
//        repository.deleteById(id);
//    }
}

