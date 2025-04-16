
package com.chh.trustfort.accounting.constant;

/**
 *
 * @author dofoleta
 */
public enum  CustomerUpdateType {
    PASSWORD("PASSWORD"),
    PIN("PIN"),
    STATUS("STATUS"),
    MARITAL_STATUS("MARITAL STATUS");

    private final String updateType;

    public String getUpdateType() {
        return this.updateType;
    }

    CustomerUpdateType(String updateType) {
        this.updateType = updateType;
    }
}
