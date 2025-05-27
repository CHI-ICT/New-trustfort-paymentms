package com.chh.trustfort.accounting.Utility;

import com.chh.trustfort.accounting.model.PayableInvoice;
import com.chh.trustfort.accounting.model.PurchaseOrder;
import com.chh.trustfort.accounting.model.Contract;
import com.chh.trustfort.accounting.repository.PurchaseOrderRepository;
import com.chh.trustfort.accounting.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class InvoiceValidationUtil {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final ContractRepository contractRepository;

    public boolean isInvoiceMatchingPOOrContract(PayableInvoice invoice) {
        Optional<PurchaseOrder> matchedPO = purchaseOrderRepository.findByPoNumberAndVendorEmail(
                invoice.getReference(), invoice.getVendorEmail()
        );

        Optional<Contract> matchedContract = contractRepository.findByVendorEmailAndCeilingAmount(
                invoice.getVendorEmail(), invoice.getAmount()
        );

        return matchedPO.isPresent() || matchedContract.isPresent();
    }
}
