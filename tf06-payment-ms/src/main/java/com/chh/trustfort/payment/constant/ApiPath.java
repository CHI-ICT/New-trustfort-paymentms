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
    public static final String FETCH_ALL_WALLETS= "/fetch-wallet";
    public static final String TRANSFER_FUNDS= "/transfer-funds";
    public static final String CHECK_BALANCE= "/check-balance";
    public static final String TRANSACTION_HISTORY= "/tran-history";
    public static final String WITHDRAW_FUNDS= "/withdraw-funds";
    public static final String FREEZE_WALLET= "/freeze-wallet";
    public static final String UNFREEZE_WALLET= "/unfreeze-wallet";
    public static final String CLOSE_WALLET= "/close-wallet";
    public static final String LOCK_FUNDS= "/lock-funds";
    public static final String UNLOCK_FUNDS= "/unlock-funds";
    public static final String UPDATE_WALLET_BALANCE ="/update-wallet-ballance";
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
    public static final String GENERATE_BALANCE_SHEET ="/generate-income-statement";
    public static final String TEST_EMAIL_NOTIFICATION="/send-test-email";
    public static final String DECRYPT = "/decrypt";

   //=======================================================Approval Rule======================================
    public static final String GET_ALL_APPROVAL_RULES = "/approval-rules";
    public static final String CREATE_APPROVAL_RULE = "/approval-rules";
    public static final String UPDATE_APPROVER = "/approval-rules/{ruleId}/approver/{newApproverId}";
    public static final String DELETE_APPROVAL_RULE = "/approval-rule/delete";

    //=====================================================Credit Approvals======================================
    public static final String ACT_ON_APPROVAL = "/credit-approvals/act";
    public static final String GET_PENDING_APPROVALS = "/credit-approvals/pending/{approverId}";

    //===================================================== Credit Lines======================================
    public static final String CREATE_CREDIT_LINE = "/credit-lines";
    public static final String GET_ALL_CREDIT_LINES = "/credit-lines";
    public static final String GET_CREDIT_LINE_BY_ID = "/credit-lines/{id}";
    public static final String UPDATE_CREDIT_LINE = "/credit-lines/{id}";

    public static final String CREDIT_WALLET ="/credit-wallet" ;
    public static final String INITIATE_FLW_PAYMENT = "/initiate-flw-payment";
    public static final String GET_ALL_WALLETS_BY_USER_ID = "/wallets/user";
    public static final String FLUTTERWAVE_WEBHOOK = "/webhook/flutterwave";
    public static final String VERIFY_FLW_PAYMENT = "/verify-flutterwave-payment";
    public static final String REVERIFY_FLUTTER = "/test-reverify";
    public static final String ENCRYPT_PAYLOAD= "/test/encrypt-payload";

    public static final String FLUTTERWAVE_REDIRECT = "/flutterwave-redirect";
    public static final String RETRY_FLW_PAYMENT = "/retry-flutterwave-payment";
    public static final String REVERIFY_FAILED_TX= "/admin/reverify-payment";
    public static final String INTERNAL_CHECK_BALANCE = "/wallet/internal/check-balance";
    public static final String PAYSTACK_WEBHOOK = "/webhook-paystack";
    public static final String INITIATE_PAYSTACK_PAYMENT = "/paystack/initiate";
    public static final String VERIFY_PAYSTACK_PAYMENT = "/paystack/verify";
    public static final String REVERIFY_PAYSTACK = "/paystack/reverify";
    public static final String RECONCILE_BANK_TRANSFER ="reconcile-bank-transfer";

}
