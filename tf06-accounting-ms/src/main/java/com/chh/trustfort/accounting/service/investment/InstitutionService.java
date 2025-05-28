package com.chh.trustfort.accounting.service.investment;

import com.chh.trustfort.accounting.dto.investment.InstitutionRequestDTO;
import com.chh.trustfort.accounting.dto.investment.InstitutionResponseDTO;
import com.chh.trustfort.accounting.model.Institution;

import java.util.List;
import java.util.Optional;

public interface InstitutionService {
    InstitutionResponseDTO createInstitution(InstitutionRequestDTO dto);
    List<Institution> getAll();
    Optional<Institution> getById(Long id);
    InstitutionResponseDTO updateInstitution(Long id, Institution update);
}
