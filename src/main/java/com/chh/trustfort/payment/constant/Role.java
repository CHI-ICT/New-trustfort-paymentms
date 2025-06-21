/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.payment.constant;

/**
 *
 * @author Daniel Ofoleta
 */
public class Role {

    /**
     * This class includes the roles
     */
    public static final String CUSTOMER_INPUT = "C_INP";
    public static final String CUSTOMER_AUTH = "C_AUTH";
    public static final String CUSTOMER_ENQ = "C_ENQ";
    public static final String CUSTOMER_MSG = "C_MSG";
    public static final String BVN_ENQ = "B_ENQ";

    // Wallet-related roles
    public static final String CREATE_WALLET = "C_WAL";
    public static final String FUND_WALLET = "F_WAL";
    public static final String FETCH_WALLET = "R_WAL";
    public static final String TRANSFER_FUNDS = "T_FUNDS";
    public static final String WITHDRAW_FUNDS = "W_FUNDS";
    public static final String CHECK_BALANCE = "W_BAL";
    public static final String TRANSACTION_HISTORY = "W_TXN";
    public static final String FREEZE_WALLET = "W_FREEZE";
    public static final String UNFREEZE_WALLET = "W_UNFREEZE";
    public static final String CLOSE_WALLET = "W_CLOSE";
    public static final String LOCK_FUNDS = "W_LOCK";
    public static final String UNLOCK_FUNDS = "W_UNLOCK";

    // Commission-related roles
    public static final String CREDIT_COMMISSION = "C_COMM";
    public static final String VIEW_COMMISSION = "V_COMM";

    public static final String GENERATE_PAYMENT_REFERENCE = "G_REF";
}
