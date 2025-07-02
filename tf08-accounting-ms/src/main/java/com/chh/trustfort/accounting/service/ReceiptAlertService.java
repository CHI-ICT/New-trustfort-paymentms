package com.chh.trustfort.accounting.service;


import com.chh.trustfort.accounting.model.AppUser;
import com.chh.trustfort.accounting.model.Receipt;
import java.util.List;

public interface ReceiptAlertService {
    String getPendingReceipts(AppUser appUser);
}