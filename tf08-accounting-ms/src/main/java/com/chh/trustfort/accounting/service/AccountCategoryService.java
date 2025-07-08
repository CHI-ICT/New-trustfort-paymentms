package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.component.ResponseCode;
import com.chh.trustfort.accounting.model.AccountCategory;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.payload.AccountCategoryRequest;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.repository.AccountCategoryRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountCategoryService {

    private final AccountCategoryRepository repository;
    private final AesService aesService;
    private final Gson gson;
    private final MessageSource messageSource;


    public String createCategory(AccountCategoryRequest request, AppUser appUser) {
        OmniResponsePayload response = new OmniResponsePayload();
        response.setResponseCode(ResponseCode.FAILED_TRANSACTION.getResponseCode());
        response.setResponseMessage(messageSource.getMessage("failed", null, Locale.ENGLISH));

        if (request == null || request.getName() == null) {
            log.warn("❌ Invalid request: missing category name");
            response.setResponseMessage("Category name is required.");
            return aesService.encrypt(gson.toJson(response), appUser);
        }

        if (repository.findByName(request.getName()).isPresent()) {
            log.warn("⚠️ Category already exists: {}", request.getName());
            response.setResponseMessage("Category already exists.");
            return aesService.encrypt(gson.toJson(response), appUser);
        }

        AccountCategory category = AccountCategory.builder()
                .name(request.getName())
                .minCode(request.getMinCode())
                .maxCode(request.getMaxCode())
                .description(request.getDescription())
                .build();

        category = repository.save(category);

        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
        response.setResponseMessage("Category created successfully.");
        response.setData(category);

        return aesService.encrypt(gson.toJson(response), appUser);
    }

    public String getAllCategories(AppUser appUser) {
        OmniResponsePayload response = new OmniResponsePayload();
        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
        response.setResponseMessage("All categories fetched.");
        response.setData(repository.findAll());
        return aesService.encrypt(gson.toJson(response), appUser);
    }
    public String getCategoryById(Long id, AppUser appUser) {
        OmniResponsePayload response = new OmniResponsePayload();
        response.setResponseCode(ResponseCode.FAILED_TRANSACTION.getResponseCode());
        response.setResponseMessage("Category not found");

        Optional<AccountCategory> optional = repository.findById(id);
        if (optional.isEmpty()) {
            log.warn("❌ No Account Category found for ID: {}", id);
            return aesService.encrypt(gson.toJson(response), appUser);
        }

        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
        response.setResponseMessage("Category retrieved successfully");
        response.setData(optional.get());

        return aesService.encrypt(gson.toJson(response), appUser);
    }

    public String updateCategory(Long id, AccountCategoryRequest req, AppUser appUser) {
        OmniResponsePayload response = new OmniResponsePayload();
        response.setResponseCode(ResponseCode.FAILED_TRANSACTION.getResponseCode());
        response.setResponseMessage("Category not found");

        AccountCategory category = repository.findById(id)
                .orElse(null);

        if (category == null) {
            log.warn("❌ Cannot update: Category ID {} not found", id);
            return aesService.encrypt(gson.toJson(response), appUser);
        }

        category.setName(req.getName());
        category.setMinCode(req.getMinCode());
        category.setMaxCode(req.getMaxCode());
        category.setDescription(req.getDescription());

        repository.save(category);

        response.setResponseCode(ResponseCode.SUCCESS.getResponseCode());
        response.setResponseMessage("Category updated successfully");
        response.setData(category);

        return aesService.encrypt(gson.toJson(response), appUser);
    }


//    public void deleteCategory(Long id) {
//        if (!repository.existsById(id)) {
//            throw new IllegalArgumentException("Category not found");
//        }
//        repository.deleteById(id);
//    }
}

