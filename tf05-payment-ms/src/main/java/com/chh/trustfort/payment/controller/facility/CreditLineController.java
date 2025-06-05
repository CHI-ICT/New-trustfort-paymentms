package com.chh.trustfort.payment.controller.facility;

import com.chh.trustfort.payment.constant.ApiPath;
import com.chh.trustfort.payment.dto.CreditLineRequestDto;
import com.chh.trustfort.payment.dto.CreditLineResponseDto;
import com.chh.trustfort.payment.service.facility.CreditLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPath.BASE_API)
public class CreditLineController {

    @Autowired
    private CreditLineService creditLineService;

    @PostMapping(value = ApiPath.CREATE_CREDIT_LINE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreditLineResponseDto> create(@RequestBody CreditLineRequestDto dto) {
        return ResponseEntity.ok(creditLineService.create(dto));
    }

    @GetMapping(value = ApiPath.GET_ALL_CREDIT_LINES, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CreditLineResponseDto>> getAll() {
        return ResponseEntity.ok(creditLineService.getAll());
    }

    @GetMapping(value = ApiPath.GET_CREDIT_LINE_BY_ID, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreditLineResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(creditLineService.getById(id));
    }

    @PutMapping(value = ApiPath.UPDATE_CREDIT_LINE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreditLineResponseDto> update(@PathVariable Long id, @RequestBody CreditLineRequestDto dto) {
        return ResponseEntity.ok(creditLineService.update(id, dto));
    }
}
