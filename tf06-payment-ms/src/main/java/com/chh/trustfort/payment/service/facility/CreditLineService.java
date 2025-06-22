package com.chh.trustfort.payment.service.facility;

import com.chh.trustfort.payment.dto.CreditLineRequestDto;
import com.chh.trustfort.payment.dto.CreditLineResponseDto;

import java.util.List;

public interface CreditLineService {

    CreditLineResponseDto create(CreditLineRequestDto requestDto);

    List<CreditLineResponseDto> getAll();

    CreditLineResponseDto getById(Long id);

    CreditLineResponseDto update(Long id, CreditLineRequestDto requestDto);
}