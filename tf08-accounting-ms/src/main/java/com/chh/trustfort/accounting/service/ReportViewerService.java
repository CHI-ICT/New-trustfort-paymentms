package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.Responses.ReportViewerResponse;
import com.chh.trustfort.accounting.dto.StatementFilterDTO;


import java.util.List;

public interface ReportViewerService {
//    List<ReportViewerResponse> getReportData(String reportTypeStr, StatementFilterDTO filter);
List<ReportViewerResponse> getReportData(String reportTypeStr, StatementFilterDTO filter);

}
