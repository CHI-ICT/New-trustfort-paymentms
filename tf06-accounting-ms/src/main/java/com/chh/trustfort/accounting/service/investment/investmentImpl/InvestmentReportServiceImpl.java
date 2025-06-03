package com.chh.trustfort.accounting.service.investment.investmentImpl;

import com.chh.trustfort.accounting.dto.ReportExportRequestDTO;
import com.chh.trustfort.accounting.model.InvestmentReportExportLog;
import com.chh.trustfort.accounting.repository.InvestmentReportExportLogRepository;
import com.chh.trustfort.accounting.repository.InvestmentRepository;
import com.chh.trustfort.accounting.service.investment.InvestmentReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class InvestmentReportServiceImpl implements InvestmentReportService {

    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private InvestmentReportExportLogRepository exportLogRepository;

    @Override
    public byte[] exportReport(ReportExportRequestDTO dto) {
//        List<Investment> investments = investmentRepository.findAll();
//
//        StringBuilder builder = new StringBuilder();
//        builder.append("Reference,Amount,ExpectedReturn,MaturityDate");
//        for (Investment inv : investments) {
//            builder.append(inv.getReference()).append(",")
//                    .append(inv.getAmount()).append(",")
//                    .append(inv.getExpectedReturn()).append(",")
//                    .append(inv.getMaturityDate()).append(" ");
//        }
//        return builder.toString().getBytes(StandardCharsets.UTF_8);
        return new byte[0];
    }

    @Override
    public void logExport(ReportExportRequestDTO dto) {
        InvestmentReportExportLog log = new InvestmentReportExportLog();
        log.setExportedBy(dto.requestedBy);
        log.setReportType(dto.reportType);
        log.setFormat(dto.format);
        log.setExportedAt(LocalDateTime.now());
        exportLogRepository.save(log);
    }
}