/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.accounting.component;

/**
 *
 * @author Daniel Ofoleta
 */
public enum Role {
    CREATE_WALLET("C_WAL"),
    CFREATE_WALLET("C_WAL"),
    FUND_WALLET("F_WAL"),
    FETCH_WALLET("W_FET"),
    TRANSFER_FUNDS("T_FND"),
    CHECK_BALANCE("W_BAL"),
    TRANSACTION_HISTORY("T_HIS"),
    WITHDRAW_FUNDS("W_FND"),
    FREEZE_WALLET("W_FRZ"),
    UNFREEZE_WALLET("W_UFR"),
    CLOSE_WALLET("W_CLS"),
    LOCK_FUNDS("L_FND"),
    UNLOCK_FUNDS("U_FND"),
    CREDIT_COMMISSION("CREDIT_COMMISSION"),
    VIEW_COMMISSION("VIEW_COMMISSION"),
    GENERATE_PAYMENT_REFERENCE("G_REF"),
    GENERATE_OTP("G_OTP"),
    MOCK_FCMB_BASE("M_FCMB"),
    SIMULATE_TRANSFER_STATUS("S_TS"),
    FUND_WEBHOOK("F_WH"),
    VALIDATE_PIN("V_PN"),
    SETUP_PIN("S_PN"),
    HANDLE_WEBHOOK("H_WB"),
    HANDLE_FCMB_WEBHOOK("H_FWB"),
    INITIATE_CARD_PAYMENT("I_CP"),
    GENERATE_ACCOUNT("G_AT"),
    CONFIRM_TRANSFER("C_TF"),
    CONFIRM_BANK_TRANSFER("C_BT"),
    VERIFY_FLW_TRANSACTION("V_FT"),
    GENERATE_BALANCE_SHEET("G_BS"),
    JOURNAL_ENTRY("J_E"),
    CREATE_CHART_OF_ACCOUNT("C_C_A"),
    CASH_FLOW_STATEMENT("C_F_S"),
    EQUITY_STATEMENT("E_S"),
    GET_INCOME_STATEMENT("G_I_S"),
    VALIDATE_STATEMENT_INTEGRITY("V_S_I"),
    EXPORT_INCOME_STATEMENT("E_I_S"),
    EXPORT_BALANCE_SHEET("E_B_S"),
    EXPORT_CASH_FLOW("E_C_F"),
    EXPORT_EQUITY_STATEMENT("E_E_S"),
<<<<<<< HEAD
    EXPORT_ALL_STATEMENTS("E_A_S"),
    RECONCILE_TAX("R_T"),
    TAX_FINANCE("T_F"),
    FILING_REPORT("F_R"),
    RECONCILIATION("R"),
    EXPORT_FILING_REPORT("E_F_R"),
    ALERTS("A"),
    SYNC_BANK_INFLOW("S_B_I"),
    GENERATE_RECEIPT("G_R"),
    ALERT_PENDING_RECEIPTS("A_PR");



=======
    EXPORT_ALL_STATEMENTS("E_A_S");
    UNLOCK_FUNDS("U_FND");
>>>>>>> 433fa6006bb5e7a12e876861edcbefb115b3ca5e

    private final String role;

    private Role(String role) {
        this.role = role;
    }

    public String getValue() {
        return this.role;
    }

}
