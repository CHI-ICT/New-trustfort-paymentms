package com.chh.trustfort.accounting.service.investment.investmentImpl;

import com.chh.trustfort.accounting.dto.investment.InstitutionRequestDTO;
import com.chh.trustfort.accounting.dto.investment.InstitutionResponseDTO;
import com.chh.trustfort.accounting.model.Institution;
import com.chh.trustfort.accounting.repository.InstitutionRepository;
import com.chh.trustfort.accounting.service.investment.InstitutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InstitutionServiceImpl implements InstitutionService {

    @Autowired
    private InstitutionRepository institutionRepository;

    public InstitutionResponseDTO createInstitution(InstitutionRequestDTO dto) {
        if (institutionRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Institution with name '" + dto.getName() + "' already exists.");
        }

        Institution inst = new Institution();
        inst.setName(dto.getName());
        inst.setCode(dto.getCode());
        inst.setContactEmail(dto.getContactEmail());
        inst.setContactPhone(dto.getContactPhone());
        inst.setAddress(dto.getAddress());
        inst.setCreatedBy(dto.getCreatedBy());
        inst.setCreatedAt(LocalDateTime.now());

        Institution saved = institutionRepository.save(inst);

        InstitutionResponseDTO response = new InstitutionResponseDTO();
        response.setId(saved.getId());
        response.setName(saved.getName());
        response.setCode(saved.getCode());
        response.setContactEmail(saved.getContactEmail());
        response.setContactPhone(saved.getContactPhone());
        response.setAddress(saved.getAddress());
        response.setCreatedBy(saved.getCreatedBy());
        return response;
    }

    public List<Institution> getAll() {
        return institutionRepository.findAll();
    }

    public Optional<Institution> getById(Long id) {
        return institutionRepository.findById(id);
    }

    public InstitutionResponseDTO updateInstitution(Long id, Institution update) {
        return institutionRepository.findById(id).map(existing -> {
            existing.setCode(update.getCode());
            existing.setContactEmail(update.getContactEmail());
            existing.setContactPhone(update.getContactPhone());
            existing.setAddress(update.getAddress());
            existing.setUpdatedBy(update.getUpdatedBy());
            existing.setUpdatedAt(LocalDateTime.now());
            Institution saved = institutionRepository.save(existing);

            InstitutionResponseDTO response = new InstitutionResponseDTO();
            response.setId(saved.getId());
            response.setName(saved.getName());
            response.setCode(saved.getCode());
            response.setContactEmail(saved.getContactEmail());
            response.setContactPhone(saved.getContactPhone());
            response.setAddress(saved.getAddress());
            response.setUpdatedBy(saved.getUpdatedBy());
            response.setUpdatedAt(LocalDateTime.now());
            return response;
        }).orElseThrow(() -> new IllegalArgumentException("Institution not found"));
    }
}
