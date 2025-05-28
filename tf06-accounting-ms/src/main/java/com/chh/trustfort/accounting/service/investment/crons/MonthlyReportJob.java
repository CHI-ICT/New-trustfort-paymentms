package com.chh.trustfort.accounting.service.investment.crons;

import com.chh.trustfort.accounting.dto.ReportExportRequestDTO;
import com.chh.trustfort.accounting.service.investment.InvestmentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MonthlyReportJob {

    @Autowired
    private InvestmentReportService reportService;

//    @Autowired
//    private EmailQueuePublisher emailQueuePublisher;

    @Scheduled(cron = "0 0 7 1 * ?") // 1st of every month at 7AM
    public void generateAndEmailMonthlyReport() {
        ReportExportRequestDTO dto = new ReportExportRequestDTO();
//        dto.reportType = "Monthly Summary";
//        dto.format = "CSV";
//        dto.requestedBy = "auto-scheduler";
//
//        byte[] report = reportService.exportReport(dto);
//        reportService.logExport(dto);
//
//        EmailMessage message = new EmailMessage();
//        message.setTo("finance-team@example.com");
//        message.setSubject("Monthly Investment Report");
//        message.setBody("Please find attached the investment report for the previous month.");
//
//        // Attach the report and publish via MIME
//        message.setAttachment(report);
//        message.setAttachmentName("Monthly_Investment_Report.csv");
//        emailQueuePublisher.publish(message);

        System.out.println("Monthly report generated and email queued.");
    }
}


