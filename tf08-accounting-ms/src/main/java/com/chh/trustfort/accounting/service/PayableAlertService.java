package com.chh.trustfort.accounting.service;

import com.chh.trustfort.accounting.model.AppUser;

import java.util.List;

public interface PayableAlertService {
    String generateAlerts(AppUser user);
}
