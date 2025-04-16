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
    
    
    
}
