package com.chh.trustfort.accounting.enums;

/**
 * Status of a tax obligation.
 */
public enum TaxStatus {
    PENDING,      // Tax recorded but not yet filed
    FILED,        // Tax return has been filed
    PAID,         // Tax amount has been fully paid
    OVERDUE,      // Tax is overdue for filing or payment
    DISPUTED      // Tax record has issues or discrepancies
}
