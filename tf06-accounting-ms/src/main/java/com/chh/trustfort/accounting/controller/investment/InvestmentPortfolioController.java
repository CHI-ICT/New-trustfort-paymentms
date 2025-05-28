package com.chh.trustfort.accounting.controller.investment;

import com.chh.trustfort.accounting.dto.investment.InvestmentRequestDTO;
import com.chh.trustfort.accounting.model.Investment;
import com.chh.trustfort.accounting.service.investment.investmentImpl.InvestmentPortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/api/investments")
public class InvestmentPortfolioController {
    @Autowired
    private InvestmentPortfolioService investmentService;

    /**
     * Create a new investment.
     */
    @PostMapping
    public ResponseEntity<Investment> createInvestment(@RequestBody InvestmentRequestDTO dto){
//                                                       @RequestParam String user) {
        Investment created = investmentService.createInvestment(dto);
        return ResponseEntity.ok(created);
    }

    /**
     * Rollover a matured investment.
     */
    @PostMapping("/{id}/rollover")
    public ResponseEntity<Investment> rollOverInvestment(@PathVariable Long id,
                                                         @RequestParam String user,
                                                         @RequestParam String role) {
        try {
            Investment rolledOver = investmentService.rollOverInvestment(id, user, role);
            return ResponseEntity.ok(rolledOver);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
