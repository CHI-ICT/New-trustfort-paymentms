package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.model.Contract;

import java.util.List;

public interface ContractService {
    Contract create(Contract contract);
    List<Contract> getAll();
}
