package com.chh.trustfort.accounting.service.serviceImpl;

import com.chh.trustfort.accounting.dto.CreateReceivableRequest;
import com.chh.trustfort.accounting.enums.DebitNoteStatus;
import com.chh.trustfort.accounting.enums.ReceivableStatus;
import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.DebitNote;
import com.chh.trustfort.accounting.model.Receivable;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.repository.DebitNoteRepository;
import com.chh.trustfort.accounting.repository.ReceivableRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.ReceivableService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReceivableServiceImpl implements ReceivableService {

    private final ReceivableRepository receivableRepository;
    private final DebitNoteRepository debitNoteRepository;
    private final AesService aesService;
    private final Gson gson;

    @Override
    public String createReceivable(CreateReceivableRequest request, AppUser appUser) {
        OmniResponsePayload response = new OmniResponsePayload();

        try {
            boolean exists = receivableRepository.existsByPayerEmailAndCurrency(
                    request.getPayerEmail(), request.getCurrency()
            );

            if (exists) {
                response.setResponseCode("06");
                response.setResponseMessage("Receivable already exists for this customer and currency.");
                return aesService.encrypt(gson.toJson(response), appUser);
            }

            // Step 1: Save Debit Note
            DebitNote debitNote = DebitNote.builder()
                    .reference("DN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .payerEmail(request.getPayerEmail())
                    .customerName(request.getCustomerName())
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .status(DebitNoteStatus.PENDING)
                    .issueDate(LocalDate.now())
                    .dueDate(request.getDueDate())
                    .createdBy("system")
                    .createdAt(LocalDateTime.now())
                    .build();

            debitNote = debitNoteRepository.save(debitNote);

            // Step 2: Save Receivable
            Receivable receivable = new Receivable();
            receivable.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            receivable.setReference("REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            receivable.setCustomerName(request.getCustomerName());
            receivable.setPayerEmail(request.getPayerEmail());
            receivable.setCustomerAccount(request.getCustomerAccount());
            receivable.setCurrency(request.getCurrency());
            receivable.setAmount(request.getAmount());
            receivable.setBalance(request.getAmount());
            receivable.setStatus(ReceivableStatus.PENDING);
            receivable.setDueDate(request.getDueDate());
            receivable.setCreatedAt(LocalDateTime.now());
            receivable.setCreatedBy("system");
            receivable.setDebitNote(debitNote);

            Receivable saved = receivableRepository.save(receivable);

            response.setResponseCode("00");
            response.setResponseMessage("Receivable created successfully");
            response.setData(saved);
        } catch (Exception e) {
            log.error("❌ Error creating receivable: {}", e.getMessage(), e);
            response.setResponseCode("06");
            response.setResponseMessage("Failed to create receivable: " + e.getMessage());
        }

        return aesService.encrypt(gson.toJson(response), appUser);
    }

    @Override
    public String getAllReceivables(AppUser appUser) {
        OmniResponsePayload response = new OmniResponsePayload();

        try {
            List<Receivable> data = receivableRepository.findAll();
            response.setResponseCode("00");
            response.setResponseMessage("Receivables fetched");
            response.setData(data);
        } catch (Exception e) {
            log.error("❌ Failed to fetch receivables", e);
            response.setResponseCode("06");
            response.setResponseMessage("Error fetching receivables");
        }

        return aesService.encrypt(gson.toJson(response), appUser);
    }
}
