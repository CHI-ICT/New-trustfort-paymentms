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

//    public boolean isInvoiceMatchingPOOrContract(PayableInvoice invoice) {
//        Optional<PurchaseOrder> matchedPO = purchaseOrderRepository.findByPoNumberAndVendorEmail(
//                invoice.getReference(), invoice.getVendorEmail()
//        );
//
//        Optional<Contract> matchedContract = contractRepository.findByVendorEmailAndCeilingAmount(
//                invoice.getVendorEmail(), invoice.getAmount()
//        );
//
//        return matchedPO.isPresent() || matchedContract.isPresent();
//    }
public boolean isInvoiceMatchingPOOrContract(PayableInvoice invoice) {
    // 1. Match Purchase Order by Vendor Email and Description
    Optional<PurchaseOrder> matchedPO = purchaseOrderRepository
            .findByVendorEmailAndDescriptionAndAmount(
                    invoice.getVendorEmail(),
                    invoice.getDescription(),
                    invoice.getAmount()
            );

    // 2. Match Contract by Vendor Email and ensure invoice amount is within ceiling and date range
    Optional<Contract> matchedContract = contractRepository
            .findByVendorEmail(invoice.getVendorEmail())
            .stream()
            .filter(c -> c.getCeilingAmount().compareTo(invoice.getAmount()) >= 0 &&
                    invoice.getInvoiceDate() != null &&
                    (invoice.getInvoiceDate().isAfter(c.getStartDate()) || invoice.getInvoiceDate().isEqual(c.getStartDate())) &&
                    (invoice.getInvoiceDate().isBefore(c.getEndDate()) || invoice.getInvoiceDate().isEqual(c.getEndDate())))
            .findFirst();

    return matchedPO.isPresent() || matchedContract.isPresent();
}

}
