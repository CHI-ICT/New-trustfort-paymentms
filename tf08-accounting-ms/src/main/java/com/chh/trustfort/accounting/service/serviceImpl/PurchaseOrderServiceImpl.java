package com.chh.trustfort.accounting.service.serviceImpl;

import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.PurchaseOrder;
import com.chh.trustfort.accounting.payload.OmniResponsePayload;
import com.chh.trustfort.accounting.repository.PurchaseOrderRepository;
import com.chh.trustfort.accounting.security.AesService;
import com.chh.trustfort.accounting.service.PurchaseOrderService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final AesService aesService;
    private final Gson gson;

    @Override
    public String createPurchaseOrder(PurchaseOrder po, AppUser appUser) {
        OmniResponsePayload response = new OmniResponsePayload();

        try {
            log.info("📦 Creating Purchase Order: {}", po.getPoNumber());

            if (purchaseOrderRepository.existsByPoNumber(po.getPoNumber())) {
                response.setResponseCode("99");
                response.setResponseMessage("Purchase Order with this PO number already exists.");
                return aesService.encrypt(gson.toJson(response), appUser);
            }

            boolean duplicate = purchaseOrderRepository.existsByVendorEmailAndDescriptionAndAmount(
                    po.getVendorEmail(), po.getDescription(), po.getAmount()
            );
            if (duplicate) {
                response.setResponseCode("99");
                response.setResponseMessage("Duplicate PO exists for this vendor, description, and amount.");
                return aesService.encrypt(gson.toJson(response), appUser);
            }

            PurchaseOrder saved = purchaseOrderRepository.save(po);
            response.setResponseCode("00");
            response.setResponseMessage("Purchase order created successfully");
            response.setData(saved);
            return aesService.encrypt(gson.toJson(response), appUser);

        } catch (Exception e) {
            log.error("❌ Error during purchase order creation: {}", e.getMessage());
            response.setResponseCode("99");
            response.setResponseMessage("Error: " + e.getMessage());
            return aesService.encrypt(gson.toJson(response), appUser);
        }
    }

    @Override
    public String getAllPurchaseOrders(AppUser appUser) {
        OmniResponsePayload response = new OmniResponsePayload();
        try {
            List<PurchaseOrder> allOrders = purchaseOrderRepository.findAll();
            response.setResponseCode("00");
            response.setResponseMessage("Purchase orders fetched successfully");
            response.setData(allOrders);
            return aesService.encrypt(gson.toJson(response), appUser);
        } catch (Exception e) {
            log.error("❌ Error fetching purchase orders: {}", e.getMessage());
            response.setResponseCode("99");
            response.setResponseMessage("Error: " + e.getMessage());
            return aesService.encrypt(gson.toJson(response), appUser);
        }
    }
}
