package com.chh.trustfort.accounting.service.investment;

import com.chh.trustfort.accounting.model.InvestmentAuditTrail;
import com.chh.trustfort.accounting.repository.InvestmentAuditTrailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InvestmentAuditService {
    @Autowired
    private InvestmentAuditTrailRepository repo;
    public void logAction(Long id, String action, String user, String details) {
        InvestmentAuditTrail audit = new InvestmentAuditTrail();
        audit.setInvestmentId(id);
        audit.setAction(action);
        audit.setPerformedBy(user);
        audit.setPerformedAt(LocalDateTime.now());
        audit.setDetails(details);
        repo.save(audit);
    }
}
