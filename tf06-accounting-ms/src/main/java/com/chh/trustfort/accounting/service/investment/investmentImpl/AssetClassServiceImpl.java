package com.chh.trustfort.accounting.service.investment.investmentImpl;

import com.chh.trustfort.accounting.dto.investment.AssetClassRequestDTO;
import com.chh.trustfort.accounting.dto.investment.AssetClassResponseDTO;
import com.chh.trustfort.accounting.model.AssetClass;
import com.chh.trustfort.accounting.repository.AssetClassRepository;
import com.chh.trustfort.accounting.service.investment.AssetClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AssetClassServiceImpl implements AssetClassService {

    @Autowired
    private AssetClassRepository assetClassRepository;

    public AssetClassResponseDTO createAssetClass(AssetClassRequestDTO dto) {
        if (assetClassRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Asset class with name '" + dto.getName() + "' already exists.");
        }

        AssetClass assetClass = new AssetClass();
        assetClass.setName(dto.getName());
        assetClass.setAverageReturnRate(dto.getAverageReturnRate());
        assetClass.setRiskLevel(dto.getRiskLevel());
        assetClass.setRegulatorCode(dto.getRegulatorCode());

        AssetClass saved = assetClassRepository.save(assetClass);

        AssetClassResponseDTO response = new AssetClassResponseDTO();
        response.setId(saved.getId());
        response.setName(saved.getName());
        response.setAverageReturnRate(saved.getAverageReturnRate());
        response.setRiskLevel(saved.getRiskLevel());
        response.setRegulatorCode(saved.getRegulatorCode());

        return response;
    }

    public List<AssetClass> getAllAssetClasses() {
        return assetClassRepository.findAll();
    }

    public Optional<AssetClass> getById(Long id) {
        return assetClassRepository.findById(id);
    }

    public AssetClass updateAssetClass(Long id, AssetClass updated) {
        return assetClassRepository.findById(id).map(existing -> {
            existing.setAverageReturnRate(updated.getAverageReturnRate());
            existing.setRiskLevel(updated.getRiskLevel());
            existing.setRegulatorCode(updated.getRegulatorCode());
            return assetClassRepository.save(existing);
        }).orElseThrow(() -> new IllegalArgumentException("AssetClass not found"));
    }
}


