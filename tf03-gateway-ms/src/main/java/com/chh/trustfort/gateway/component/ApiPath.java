package com.chh.trustfort.gateway.component;

import org.springframework.stereotype.Component;

/**
 *
 * @author Daniel Ofoleta
 */
@Component
public class ApiPath {

    
    public static final String BASE_API = "/trustfort/api/v1";
    public static final String HEADER_STRING = "Authorization";
    public static final String ID_TOKEN = "id-token";
    public static final String TOKEN_PREFIX = "Bearer";
    
    public static final String USER_LOGIN = "/user/login";
    
    
    
    
    /**
     * This class includes the name and API end points of other microservices
     * that we need to communicate. NOTE: WRITE EVERYTHING IN ALPHABETICAL ORDER
     */
    //A
    public static final String ADMIN_BASE = "/admin";
    public static final String ADMIN_LOGIN = "/admin/login";
    public static final String ADMIN_HOME = "/admin/home";
    public static final String ACCOUNT_BALANCE = "/account/balance";
    public static final String ACCOUNT_DETAILS = "/account/details";
    public static final String ACCOUNT_STATEMENT = "/account/statement";
    public static final String ACCOUNT_LIST = "/admin/account/list";
    public static final String ACCOUNT_UPLOAD = "/admin/account/upload";
    public static final String API_DOC = "/doc";
    public static final String API_DOC_LOGIN = "/doc/login";
    public static final String ADMIN_API_DOC = "/admin/doc";
    public static final String ADMIN_API_DOC_LOGIN = "/admin/doc/login";
    //B
    

    //C
    public static final String CUSTOMER_ACCOUNTS = "/account/global";
    public static final String CUSTOMER_DETAILS = "/customer/details";
    public static final String CUSTOMER_LIST = "/admin/customer/list";
    public static final String CUSTOMER_UPLOAD = "/admin/customer/upload";
    public static final String CUSTOMER_INDIVIDUAL = "/customer/individual/new";
    public static final String CUSTOMER_CORPORATE = "/customer/corporate/new";
    public static final String CHANNEL_USER_CREATE = "/admin/user/create";
    public static final String CHANNEL_USER_LIST = "/admin/user/list";
    public static final String CHANNEL_USER_STATUS_UPDATE = "/admin/user/update/status";
    public static final String CHANNEL_USER_UPDATE = "/admin/user/update";
    public static final String CHANNEL_USER_DELETE = "/admin/user/delete";
    public static final String CHANNEL_ROLE_LIST = "/admin/role/list";
    public static final String CHANNEL_ROLE_GROUP_LIST = "/admin/role/group/list";
    public static final String CHANNEL_ROLE_GROUP = "/admin/role/group/new";
    public static final String CHANNEL_ROLE_GROUP_UPDATE = "/admin/role/group/update";
    public static final String CHANNEL_ADD_GROUP_ROLE = "/admin/group/role/add";
    public static final String CHANNEL_REMOVE_GROUP_ROLE = "/admin/group/role/remove";
    public static final String CHANNEL_GROUP_ROLE_LIST = "/admin/group/role/list";
    //D
    //E
    public static final String ENCRYPT_STRING = "/token/encrypt-text";
    //F
    public static final String FETCH_BVN = "/fetch-bvn";
    public static final String FETCH_ALL_USERS = "/fetch-all-users";
    public static final String FUNDS_TRANSFER_PROCESS = "/funds-transfer/process";
    public static final String FUNDS_TRANSFER_REVERSE = "/funds-transfer/reverse";
    public static final String FUNDS_TRANSFER_STATUS = "/funds-transfer/status";
    public static final String FUNDS_TRANSFER_LIST = "/admin/funds-transfer/list";
    public static final String FORMAT_AMOUNT = "/generic/formatted-amount";
    public static final String FETCH_PROPERTY_VALUE = "/admin/property";
    //G
    public static final String GENERATE_TOKEN = "/token/generate-token";
    public static final String GENERATE_VERSION = "/ofs/generate/version";
    public static final String GENERATE_ENQUIRY = "/ofs/generate/enquiry";
    public static final String GENERATE_OFS_ENQUIRY = "/generic/ofs/enquiry";
    public static final String GENERATE_OFS_VERSION = "/generic/ofs/version";
    public static final String GENERATE_REQUEST_LOG = "/generic/request-log";
    public static final String GENERATE_USER_ACTIVITY_LOG = "/generic/user-activity";
    //H
   
    //I
    public static final String IP_WHITE_LIST_CREATE = "/admin/ip/create";
    public static final String IP_WHITE_LIST_DELETE = "/admin/ip/delete";
    public static final String IP_WHITE_LIST = "/admin/ip/list";
    public static final String IP_BLOCK = "/ip/block";
    //J
    //K
    //L
    //M
    public static final String STATISTICS_MEMORY = "/actuator/stats";
    //N
    //O
    public static final String OPEN_SAVINGS_ACCOUNT = "/account/savings/open";
    public static final String OPEN_CURRENT_ACCOUNT = "/account/current/open";
    //P
    public static final String POSTING_DATE = "/dates/posting";
    public static final String POST_DIRECT_OFS = "/generic/post-ofs";
    //Q
    //R
    public static final String REQUEST_ENCRYPTION = "/admin/request/encrypt";
    public static final String REQUEST_ENCRYPTION_BAD = "/admin/request/encrypt/bad";
    public static final String REVERSE_TRANSACTION = "/admin/transaction/reverse";
    //S
    public static final String SAVE = "/save";
    public static final String SEARCH = "/search";
    //T
    
    //U
    public static final String UPDATE = "/update";
    public static final String UPDATE_PROPERTY_VALUE = "/admin/property/update";
    public static final String USER_LOCKED = "/user/locked";
    //V
    public static final String VALIDATE_TOKEN = "/token/validate-token";
    public static final String VETTING_TABLE_SECTOR = "/generic/sector";
    public static final String VETTING_TABLE_TARGET = "/generic/target";
    public static final String VETTING_TABLE_INDUSTRY = "/generic/industry";
    public static final String VETTING_TABLE_COUNTRY = "/generic/country";
    public static final String VETTING_TABLE_ACCOUNT_OFFICER = "/generic/account-officer";
    public static final String VETTING_TABLE_CATEGORY = "/generic/category";
    
    //W
    //X
    //Y
    //Z
}
