package com.chh.trustfort.accounting.service.investment;

import org.springframework.stereotype.Service;

@Service
public class RolloverApprovalService {
//    @Autowired
//    private RolloverRequestRepository requestRepo;
//    @Autowired private InvestmentPortfolioService portfolioService;
//    @Autowired private EmailQueuePublisher emailQueue;
//
//    public void requestRollover(Long investmentId, String user) {
//        RolloverRequest req = new RolloverRequest();
//        req.setInvestmentId(investmentId);
//        req.setRequestedBy(user);
//        req.setRequestedAt(LocalDateTime.now());
//        requestRepo.save(req);
//
//        emailQueue.publish(new EmailMessage("approver@bank.com", "Rollover Request", "Investment " + investmentId + " requested for rollover"));
//    }
//
//    public Investment approveRollover(Long requestId, String approver) {
//        RolloverRequest req = requestRepo.findById(requestId).orElseThrow();
//        if (req.isApproved()) throw new IllegalStateException("Already approved");
//
//        Investment inv = portfolioService.rollOverInvestment(req.getInvestmentId(), approver, "INVESTMENT_EXECUTIVE");
//        req.setApproved(true);
//        req.setApprovedAt(LocalDateTime.now());
//        req.setApprovedBy(approver);
//        requestRepo.save(req);
//        return inv;
//    }
}
