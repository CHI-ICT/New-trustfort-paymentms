package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.model.PurchaseOrder;

import java.util.List;

public interface PurchaseOrderService {
    PurchaseOrder create(PurchaseOrder po);
    List<PurchaseOrder> getAll();
}
