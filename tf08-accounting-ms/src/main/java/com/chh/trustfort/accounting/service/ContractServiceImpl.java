package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.model.Contract;
import com.chh.trustfort.accounting.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;

    @Override
    public Contract create(Contract contract) {
        log.info("Creating contract: {}", contract.getContractCode());

        // 🔒 Check by contract code
        if (contractRepository.existsByContractCode(contract.getContractCode())) {
            throw new RuntimeException("Contract with this code already exists.");
        }

        // 🔒 Optional: Prevent vendor + currency + amount + period duplication
        boolean exists = contractRepository.existsByVendorEmailAndCurrencyAndCeilingAmountAndStartDateAndEndDate(
                contract.getVendorEmail(),
                contract.getCurrency(),
                contract.getCeilingAmount(),
                contract.getStartDate(),
                contract.getEndDate()
        );

        if (exists) {
            throw new RuntimeException("Duplicate contract exists with similar vendor, currency, amount and date range.");
        }

        return contractRepository.save(contract);
    }


    @Override
    public List<Contract> getAll() {
        return contractRepository.findAll();
    }
}
