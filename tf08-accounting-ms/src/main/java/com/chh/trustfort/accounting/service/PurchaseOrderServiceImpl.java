package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.model.PurchaseOrder;
import com.chh.trustfort.accounting.repository.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;

    @Override
    public PurchaseOrder create(PurchaseOrder po) {
        log.info("Creating Purchase Order: {}", po.getPoNumber());

        // 🔒 Check by PO Number (unique constraint)
        if (purchaseOrderRepository.existsByPoNumber(po.getPoNumber())) {
            throw new RuntimeException("Purchase Order with this PO number already exists.");
        }

        // 🔒 Optional: Prevent duplicate vendor, description, and amount combo
        boolean duplicate = purchaseOrderRepository.existsByVendorEmailAndDescriptionAndAmount(
                po.getVendorEmail(), po.getDescription(), po.getAmount()
        );
        if (duplicate) {
            throw new RuntimeException("Duplicate PO exists for this vendor, description, and amount.");
        }

        return purchaseOrderRepository.save(po);
    }

    @Override
    public List<PurchaseOrder> getAll() {
        return purchaseOrderRepository.findAll();
    }
}
