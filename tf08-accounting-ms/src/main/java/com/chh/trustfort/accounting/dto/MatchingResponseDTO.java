package com.chh.trustfort.accounting.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchingResponseDTO {
    private String status;
    private String message;
    private String matchedPayer;
    private int matchedReceiptsCount;
    private int matchedReceivablesCount;
    private List<MatchedPairDTO> details;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
}
