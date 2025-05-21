package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.dto.CreateDisputeRequest;
import com.chh.trustfort.accounting.dto.DisputeResponse;
import com.chh.trustfort.accounting.enums.DisputeStatus;
import com.chh.trustfort.accounting.exception.ApiException;
import com.chh.trustfort.accounting.model.Dispute;
import com.chh.trustfort.accounting.repository.DisputeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisputeServiceImpl implements DisputeService {

    private final DisputeRepository disputeRepository;

    @Override
    public DisputeResponse raiseDispute(CreateDisputeRequest request) {
        Dispute dispute = Dispute.builder()
                .reference("DSP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .relatedReceiptReference(request.getRelatedReceiptReference())
                .customerEmail(request.getCustomerEmail())
                .customerName(request.getCustomerName())
                .description(request.getDescription())
                .raisedAt(LocalDateTime.now())
                .raisedBy(request.getRaisedBy())
                .status(DisputeStatus.OPEN)
                .build();

        return map(disputeRepository.save(dispute));
    }

    @Override
    public DisputeResponse resolveDispute(String reference, String resolution, String resolvedBy) {
        Dispute dispute = disputeRepository.findByReference(reference)
                .orElseThrow(() -> new ApiException("Dispute not found with reference: " + reference, HttpStatus.CONFLICT));

        dispute.setStatus(DisputeStatus.RESOLVED);
        dispute.setResolution(resolution);
        dispute.setResolvedAt(LocalDateTime.now());
        dispute.setResolvedBy(resolvedBy);

        return map(disputeRepository.save(dispute));
    }

    @Override
    public List<DisputeResponse> getAllDisputes() {
        return disputeRepository.findAll().stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    private DisputeResponse map(Dispute dispute) {
        return DisputeResponse.builder()
                .reference(dispute.getReference())
                .relatedReceiptReference(dispute.getRelatedReceiptReference())
                .customerEmail(dispute.getCustomerEmail())
                .customerName(dispute.getCustomerName())
                .description(dispute.getDescription())
                .status(dispute.getStatus())
                .resolution(dispute.getResolution())
                .raisedAt(dispute.getRaisedAt())
                .resolvedAt(dispute.getResolvedAt())
                .build();
    }
}
