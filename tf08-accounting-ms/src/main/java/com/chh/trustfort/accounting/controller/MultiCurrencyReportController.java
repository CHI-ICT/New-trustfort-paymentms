package com.chh.trustfort.accounting.controller;

import com.chh.trustfort.accounting.constant.ApiPath;
import com.chh.trustfort.accounting.dto.MultiCurrencyReportRow;
import com.chh.trustfort.accounting.service.MultiCurrencyReportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;





@RestController
@Slf4j
@RequestMapping(ApiPath.BASE_API )
@Tag(name = "Multicurrency Reports", description = "Generate financial reports and integrity checks")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class MultiCurrencyReportController {

    private final MultiCurrencyReportService multiCurrencyReportService;

    /**
     * Returns all receipts converted into the given base currency (e.g., NGN, USD)
     * @param baseCurrency Base currency to convert to (default: NGN)
     */
    @GetMapping(ApiPath.MULTI_CURRENCY_RECEIPTS)
    public List<MultiCurrencyReportRow> getAllConvertedReceipts(
            @RequestParam(defaultValue = "NGN") String baseCurrency
    ) {
        log.info("Fetching converted receipts report in base currency: {}", baseCurrency);
        return multiCurrencyReportService.getAllConvertedReceipts(baseCurrency.toUpperCase());
    }
}
