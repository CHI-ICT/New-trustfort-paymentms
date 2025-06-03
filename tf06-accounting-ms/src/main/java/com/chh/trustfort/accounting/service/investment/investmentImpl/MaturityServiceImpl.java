package com.chh.trustfort.accounting.service.investment.investmentImpl;

import com.chh.trustfort.accounting.repository.InvestmentRepository;
import com.chh.trustfort.accounting.service.investment.MaturityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class MaturityServiceImpl implements MaturityService {

    @Autowired
    private InvestmentRepository investmentRepository;

//    @Autowired
//    private EmailQueuePublisher emailQueuePublisher;

    @Override
    public void checkMaturingInvestments() {
        LocalDate today = LocalDate.now();
        LocalDate threshold = today.plusDays(7);

//        List<Investment> upcoming = investmentRepository.findByMaturityDateBetweenAndMaturityNotifiedFalse(today, threshold);

//        for (Investment inv : upcoming) {
//            // Send maturity alert email
//            EmailMessage message = new EmailMessage();
//            message.setTo("investment-team@example.com");
//            message.setSubject("Upcoming Investment Maturity");
//            message.setBody("Investment " + inv.getReference() + " is maturing on " + inv.getMaturityDate());
//            emailQueuePublisher.publish(message);
//
//            inv.setMaturityNotified(true);
//            investmentRepository.save(inv);
//
//            System.out.println("Maturity alert sent for investment: " + inv.getReference());
//        }
    }
}

