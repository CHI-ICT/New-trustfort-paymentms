package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.CreditLineResponseDto;
import com.chh.trustfort.accounting.payload.CreditLineRequestDto;

import java.util.List;

public interface CreditLineService {

    CreditLineResponseDto create(CreditLineRequestDto requestDto);

    List<CreditLineResponseDto> getAll();

    CreditLineResponseDto getById(Long id);

    CreditLineResponseDto update(Long id, CreditLineRequestDto requestDto);
}