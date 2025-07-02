package com.chh.trustfort.accounting.service.serviceImpl;

import com.chh.trustfort.accounting.dto.CreateDisputeRequest;
import com.chh.trustfort.accounting.dto.DisputeResponse;
import com.chh.trustfort.accounting.enums.DisputeStatus;
import com.chh.trustfort.accounting.exception.ApiException;
import com.chh.trustfort.accounting.model.Dispute;
import com.chh.trustfort.accounting.repository.DisputeRepository;
import com.chh.trustfort.accounting.service.DisputeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DisputeServiceImpl implements DisputeService {

    private final DisputeRepository disputeRepository;

    @Override
    public DisputeResponse raiseDispute(CreateDisputeRequest request) {
        log.info("📩 Raising dispute for receipt: {}", request.getRelatedReceiptReference());

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
