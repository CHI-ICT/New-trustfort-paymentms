package com.chh.trustfort.accounting.service.investment;

import com.chh.trustfort.accounting.dto.investment.AssetClassRequestDTO;
import com.chh.trustfort.accounting.dto.investment.AssetClassResponseDTO;
import com.chh.trustfort.accounting.model.AssetClass;

import java.util.List;
import java.util.Optional;

public interface AssetClassService {
    AssetClassResponseDTO createAssetClass(AssetClassRequestDTO dto) ;
    List<AssetClass> getAllAssetClasses();
    Optional<AssetClass> getById(Long id);
    AssetClass updateAssetClass(Long id, AssetClass updated);
}
