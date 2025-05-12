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

    
    
    
}
