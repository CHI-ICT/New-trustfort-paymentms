package com.chh.trustfort.accounting.service;


import com.chh.trustfort.accounting.model.Receipt;
import java.util.List;

public interface ReceiptAlertService {
    List<Receipt> getPendingReceipts();
}