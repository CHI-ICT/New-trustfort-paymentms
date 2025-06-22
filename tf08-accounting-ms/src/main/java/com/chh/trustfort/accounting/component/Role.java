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

    EXPORT_ALL_STATEMENTS("E_A_S"),
    RECONCILE_TAX("R_T"),
    TAX_FINANCE("T_F"),
    FILING_REPORT("F_R"),
    RECONCILIATION("R"),
    EXPORT_FILING_REPORT("E_F_R"),
    ALERTS("A"),
    SYNC_BANK_INFLOW("S_B_I"),
    GENERATE_RECEIPT("G_R"),
    ALERT_PENDING_RECEIPTS("A_PR"),
    COA_BASE("C_B"),
    CREATE("C"),
    GET_ALL("G_A"),
    DOUBLE_ENTRY("D_E"),
    MULTI_CURRENCY_RECEIPTS("M_R"),
    DEBTOR_REPORT("D_R"),
    DEBT_AGING_SUMMARY("D_A_S"),
    CREATE_RECEIVABLE("C_R"),
    GET_RECEIVABLE("G_R"),
    RAISE_DISPUTE("R_D"),
    RESOLVE_DISPUTE("RS_D"),
    GET_DISPUTES("G_D"),
    MOVE_PAYMENT("M_P"),
    GET_RECONCILIATION("G_R"),
    MATCHER("M_T"),
    CREATE_CREDIT_NOTE("C_C_N"),
    CREATE_DEBIT_NOTE("C_D_N"),
    SUBMIT_INVOICE("S_I"),
    APPROVE_INVOICE("A_I"),
    SCHEDULE_PAYMENTS("S_P"),
    DECRYPT("D"),
    CREATE_PO("C_P"),
    ALL_PO("A_P"),
    CREATE_CONTRACT("C_C"),
    ALL_CONTRACT("A_C"),
    TEST_ALERTS("T_A"),
    CREATE_INSTALLMENTS("C_I"),
    POST_TO_GL("P_T_G"),
    TRIGGER_RECONCILIATION("T_R"),
    CLASSIFY_INVOICE("C_I"),
    GET_PAYABLES_REPORT("G_P_R"),
    SIMULATE_PAYMENT("S_P"),
    FORCAST("F"),
    GET_PROFIT_AND_LOSS("G_P_L"),
    GET_BALANCE_SHEET("B_ST"),
    GET_CASH_FLOW("G_CF"),
    GET_CONSOLIDATED_SUMMARY("G_C_S"), FINANCIAL_REPORT("F_R");


    //    EXPORT_ALL_STATEMENTS("E_A_S"),
//    UNLOCK_FUNDS("U_FND");



    private final String role;

    private Role(String role) {
        this.role = role;
    }

    public String getValue() {
        return this.role;
    }

}
