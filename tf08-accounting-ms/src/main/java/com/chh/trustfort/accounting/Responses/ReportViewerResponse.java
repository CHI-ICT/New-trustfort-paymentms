package com.chh.trustfort.accounting.Responses;

import lombok.*;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportViewerResponse {
    private Map<String, Object> fields;
}
