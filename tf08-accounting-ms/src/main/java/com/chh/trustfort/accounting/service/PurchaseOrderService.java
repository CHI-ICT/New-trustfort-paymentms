package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.PurchaseOrder;

import java.util.List;

public interface PurchaseOrderService {
    String createPurchaseOrder(PurchaseOrder po, AppUser appUser);
    String getAllPurchaseOrders(AppUser appUser);
}
