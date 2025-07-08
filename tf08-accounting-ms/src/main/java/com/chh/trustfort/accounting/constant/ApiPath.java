package com.chh.trustfort.accounting.constant;

/**
 *
 * @author Daniel Ofoleta
 */
public class ApiPath {

    //=======================================================Generic======================================
     //=======================================================Generic======================================
    public static final String BASE_API = "/trustfort/api/v1";
    public static final String HEADER_STRING = "Authorization";
    public static final String ID_TOKEN = "id-token";
    public static final String TOKEN_PREFIX = "Bearer";
    
    
    //=======================================================Quotes======================================
    public static final String REQUEST_QUOTE = "/request-quote";
    public static final String PROCESS_PAYMENT = "/make-payment";
    
    //=======================================================Wallet======================================
    public static final String CREATE_WALLET= "/create-wallet";
    public static final String FUND_WALLET= "/fund-wallet";
    public static final String FETCH_WALLET= "/fetch-wallet";
    public static final String TRANSFER_FUNDS= "/transfer-funds";
    public static final String CHECK_BALANCE= "/check-balance";
    public static final String TRANSACTION_HISTORY= "/tran-history";
    public static final String WITHDRAW_FUNDS= "/withdraw-funds";
    public static final String FREEZE_WALLET= "/freeze-wallet";
    public static final String UNFREEZE_WALLET= "/unfreeze-wallet";
    public static final String CLOSE_WALLET= "/close-wallet";
    public static final String LOCK_FUNDS= "/lock-funds";
    public static final String UNLOCK_FUNDS= "/unlock-funds";
    public static final String GENERATE_BALANCE_SHEET="/balance-sheet";
    public static final String GENERATE_INCOME_STATEMENT="/generate-income-statement";
    public static final String JOURNAL_ENTRY ="/journal-entry";
    public static final String INTERNAL_POST_JOURNAL ="/internal/journal-entry";
    public static final String CREATE_CHART_OF_ACCOUNT ="/create-chart-of-account";
    public static final String CASH_FLOW_STATEMENT ="/api/finance/cash-flow";
    public static final String EQUITY_STATEMENT ="/api/finance/equity-statement";
    public static final String GET_INCOME_STATEMENT ="/statements/income";
    public static final String VALIDATE_STATEMENT_INTEGRITY ="/integrity";
    public static final String EXPORT_INCOME_STATEMENT="/income/export";
    public static final String EXPORT_BALANCE_SHEET ="/balance-sheet/export";
    public static final String EXPORT_CASH_FLOW ="/cash-flow/export";
    public static final String EXPORT_EQUITY_STATEMENT ="/equity/export";
    public static final String EXPORT_ALL_STATEMENTS="/export/all";
    public static final String RECONCILE_TAX="/tax/reconciliation";
    public static final String TAX_FINANCE="/finance/tax";
    public static final String FILING_REPORT="/filing-report";
    public static final String RECONCILIATION ="/reconciliation";
    public static final String EXPORT_FILING_REPORT ="/export-filing-report";
    public static final String ALERTS ="/alerts";
    public static final String SYNC_BANK_INFLOW ="/sync";
    public static final String GENERATE_RECEIPT="/generate";
    public static final String ALERT_PENDING_RECEIPTS = "/alerts/pending-receipts";
    public static final String COA_BASE = "/chart-of-accounts";
    public static final String CREATE = "/create";
    public static final String GET_ALL = "/all";
    public static final String DOUBLE_ENTRY = "/create-double-entry";
    public static final String MULTI_CURRENCY_RECEIPTS= "/converted-receipts";
    public static final String DEBTOR_REPORT= "/debtor-report";
    public static final String DEBT_AGING_SUMMARY = "/debt-aging-summary";
    public static final String CREATE_RECEIVABLE = "/receivables/create";
    public static final String GET_RECEIVABLE = "/receivables/all";
    public static final String RAISE_DISPUTE = "/disputes";
    public static final String RESOLVE_DISPUTE="/disputes/resolve";
    public static final String GET_DISPUTES ="/disputes";
    public static final String MOVE_PAYMENT="/payment-movement";
    public static final String GET_RECONCILIATION ="/reconciliation/run";
    public static final String MATCHER = "/auto-match";

    public static final String CREATE_CREDIT_NOTE = "/credit-notes/create";
    public static final String CREATE_DEBIT_NOTE = "/debit-notes/{oldNoteId}/replace";
    public static final String SUBMIT_INVOICE = "/submit-invoice";
    public static final String APPROVE_INVOICE ="/invoices/{invoiceId}/approve";
    public static final String SCHEDULE_PAYMENTS = "/invoices/{invoiceId}/schedule";
    public static final String DECRYPT = "/decrypt";

    public static final String CREATE_PO = "/po/create";
    public static final String ALL_PO = "/po/all";

    public static final String CREATE_CONTRACT = "/contract/create";
    public static final String ALL_CONTRACT = "/contract/all";
    public static final String TEST_ALERTS = "/test-payable-alerts";
    public static final String CREATE_INSTALLMENTS = "/installments/generate";
    public static final String POST_TO_GL = "/payables/{invoiceId}/post-to-gl";
    public static final String TRIGGER_RECONCILIATION = "/payables/reconcile";
    public static final String CLASSIFY_INVOICE = "/payables/{invoiceNumber}/classify";
    public static final String GET_PAYABLES_REPORT = "/payables/reports";
    public static final String SIMULATE_PAYMENT = "/payables/{invoiceNumber}/pay";
    public static final String  FORCAST = "/generate-cashflow-forecast";
    public static final String GET_PROFIT_AND_LOSS = "/profit-loss";
    public static final String GET_BALANCE_SHEET ="/balance-sheet";
    public static final String GET_CASH_FLOW = "/cash-flow";
    public static final String GET_CONSOLIDATED_SUMMARY ="/consolidated-summary";
    public static final String GET_TRIAL_BALANCE= "/trial-balance";
    public static final String GET_FINANCIAL_SUMMARY= "/financial-summary";
    public static final String EXPORT_REPORT = "/export-report";
    public static final String DOWNLOAD_REPORT = "/download-report";
    public static final String GET_VARIANCE_ANALYSIS= "/variance-analysis";
    public static final String GET_DASHBOARD_SUMMARY = "/dashboard-summary";
    public static final String GET_REPORT_MONITORING = "/report-monitoring";
    public static final String INITIATE_REPORT_APPROVAL = "/report-approval/initiate";
    public static final String APPROVE_REPORT = "/report-approval/approve";
    public static final String REJECT_REPORT = "/report-approval/reject";
    public static final String GET_APPROVALS_FOR_REPORT = "/report-approval/by-report";
    public static final String GET_APPROVAL_BY_ID = "/report-approval/by-id";
    public static final String GET_APPROVALS_BY_STATUS = "/report-approval/by-status";

    public static final String REPORT_VIEWER = "/viewer";
    public static final String RECONCILE_BANK = "/bank/reconcile";
    public static final String REVERSE_DEBIT_NOTE = "/reverse-debit-note";
    public static final String OVERDUE_DEBTORS = "/overdue-debtors";
    public static final String CREATE_DEPARTMENT_CODE = "/department-code/create";
    public static final String UPDATE_DEPARTMENT_CODE = "/department-code/update";
    public static final String ALL_DEPARTMENT_CODES = "/department-code/all";
    public static final String GET_DEPARTMENT_CODE_BY_CODE = "/department-code/by-code";
    public static final String DELETED_DEPARTMENT_CODES = "/department-code/deleted";
    public static final String DELETE_DEPARTMENT_CODE = "/department-code/delete";
    public static final String RESTORE_DEPARTMENT_CODE = "/department-code/restore";
    public static final String GET_ENTITY_CODES = "/get-entity-codes";
    public static final String GET_ENTITY_CODE_BY_ID = "/get-entity-code/by-id";
    public static final String UPDATE_ENTITY_CODE = "/update-entity-code/update";
    public static final String DELETE_ENTITY_CODE ="/delete-entity-code/delete" ;
    public static final String CREATE_ENTITY_CODE = "create-entity-code/create" ;
    public static final String GENERATE_EOP = "/payables/{invoiceId}/generate-eop";
    public static final String GET_EOP = "/payables/{invoiceId}/eop";

    public static final String CREATE_ACCOUNT_CATEGORY = "/account-categories/create";
    public static final String GET_ACCOUNT_CATEGORIES = "/account-categories";
    public static final String GET_ACCOUNT_CATEGORY_BY_ID = "/account-categories/get";
    public static final String UPDATE_ACCOUNT_CATEGORY = "/account-categories/update";


    public static final String ENCRYPT_PAYLOAD ="/test-encrypt-payload";
}
