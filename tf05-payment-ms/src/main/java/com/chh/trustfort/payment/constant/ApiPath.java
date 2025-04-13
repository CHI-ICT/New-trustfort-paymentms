package com.chh.trustfort.payment.constant;

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
    //=======================================================Commission======================================
    public static final String COMMISSION_BASE = "/commission";
    public static final String CREDIT_COMMISSION = "/credit-commission";
    public static final String USER_COMMISSIONS = "/user-commissions";
    public static final String WITHDRAW_COMMISSION = "/withdraw-commission";

    public static final String GENERATE_PAYMENT_REFERENCE = "/generate-reference";

    //=======================================================OTP======================================
    public static final String GENERATE_OTP = "/generate-otp";
    //=======================================================Reconciliation======================================
    public static final String RECONCILIATION_BASE = "/reconciliation";
    public static final String RETRY_FAILED_TRANSFERS = "/retry-failed-transfers";

    //=======================================================Mock FCMB======================================
    public static final String MOCK_FCMB_BASE = "/mock-fcmb";
    public static final String SIMULATE_TRANSFER_STATUS = "/notify-transfer-status";

//=======================================================FCMB WEBHOOK======================================
    public static final String FUND_WEBHOOK = "/fcmb-webhook";

    public static final String HANDLE_WEBHOOK = "/webhook-paystack";

    public static final String HANDLE_FCMB_WEBHOOK = "/handleFcmbWebhook";


    //=======================================================PIN======================================
    public static final String VALIDATE_PIN = "/validate-pin";

    public static final String SETUP_PIN = "/setup-pin";

    //=======================================================CARD PAYMENT======================================
    public static final String INITIATE_CARD_PAYMENT = "/initiate-card-payment";

    //=======================================================GENERATE ACCOUNT======================================
    public static final String GENERATE_ACCOUNT = "/generate-account";
    public static final String CONFIRM_TRANSFER = "/confirm-transfer";
    public static final String CONFIRM_BANK_TRANSFER = "/confirm-bank-transfer";
    public static final String VERIFY_FLW_TRANSACTION = "/verify-transaction";


}
