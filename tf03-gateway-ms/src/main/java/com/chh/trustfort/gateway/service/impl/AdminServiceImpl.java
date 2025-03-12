/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chh.trustfort.gateway.service.impl;

import com.chh.trustfort.gateway.service.AdminService;
import org.springframework.stereotype.Service;

/**
 *
 * @author Daniel Ofoleta
 */
@Service
public class AdminServiceImpl implements AdminService {

//    @Autowired
//    MessageSource messageSource;
//    @Autowired
//    GenericService genericService;
//    @Autowired
//    AdminRepository adminRepository;
//    @Autowired
//    JwtTokenUtil jwtToken;
//    @Autowired
//    Gson gson;
//    @Value("${omnix.version.customer}")
//    private String customerVersion;
//    @Value("${omnix.version.account}")
//    private String accountVersion;
//    @Value("${tier1.per.deposit.limit}")
//    private BigDecimal tier1DepositLimit;
//    @Value("${tier1.per.withdrawal.limit}")
//    private BigDecimal tier1WithdrawalLimit;
//    @Value("${tier1.max.balance}")
//    private BigDecimal tier1BalanceLimit;
//    @Value("${tier1.per.withdrawal.limit}")
//    private BigDecimal tier1DailyBalance;
//    @Value("${tier2.per.deposit.limit}")
//    private BigDecimal tier2DepositLimit;
//    @Value("${tier2.per.withdrawal.limit}")
//    private BigDecimal tier2WithdrawalLimit;
//    @Value("${tier2.max.balance}")
//    private BigDecimal tier2BalanceLimit;
//    @Value("${tier2.per.withdrawal.limit}")
//    private BigDecimal tier2DailyBalance;
//   @Value("${tier3.per.withdrawal.limit}")
//    private BigDecimal tier3DepositLimit;
//    @Value("${tier3.per.withdrawal.limit}")
//    private BigDecimal tier3WithdrawalLimit;
//    @Value("${tier3.max.balance}")
//    private BigDecimal tier3BalanceLimit;
//    @Value("${tier3.per.withdrawal.limit}")
//    private BigDecimal tier3DailyBalance;
//    @Value("${omnix.version.reversal}")
//    private String reveresalVersion;
//    @Value("${omnix.digital.branch.code}")
//    private String digitalBranchCode;
//    private String XPRESSPAY_TRANS_QUERY_URL = "https://reseller.payxpress.com/api/txn-requery";
//    private String XPRESS_PAY_USERNAME = "amebude@accionmfb.com";
//    private String XPRESS_PAY_PASSWORD = "Shuffle18$";
//    private String WEB_KEY_LIVE = "0b068945a0bd4957aa5defecda02e678";
//    private String WEB_KEY_TEST = "70f562d238ca46ea90dd2489ed85b964";
//    private String ACCOUNT_ID = "100011540";
//    private String AUTHORIZATION = "Basic " + Base64.getEncoder().encodeToString((XPRESS_PAY_USERNAME.concat(":").concat(XPRESS_PAY_PASSWORD)).getBytes());
//    Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

//    @Override
//    public String uploadCustomerRecord(String token, CustomerUploadRequestPayload requestPayload) {
//        String requestBy = jwtToken.getUserNameFromToken(token);
//        String userCredentials = jwtToken.getUserCredentialFromToken(token);
//        OmniResponsePayload errorResponse = new OmniResponsePayload();
//        //Create request log
//        String requestJson = gson.toJson(requestPayload);
//        genericService.generateLog("Customer Upload", token, requestJson, "API Request", "INFO", requestPayload.getRequestId());
//        String response = "";
//        BigDecimal tierDeposit = BigDecimal.ZERO, tierWithdrawal = BigDecimal.ZERO, tierBalance = BigDecimal.ZERO, tierDailyBalance = BigDecimal.ZERO;
//        CustomerResponsePayload customerResponse = new CustomerResponsePayload();
//        try {
//            //Check the channel information
//            AppUser appUser = adminRepository.getAppUserUsingUsername(requestBy);
//            if (appUser == null) {
//                //Log the error
//                genericService.generateLog("Customer Upload", token, messageSource.getMessage("appMessages.user.notexist", new Object[]{requestBy}, Locale.ENGLISH), "API Response", "INFO", requestPayload.getRequestId());
//                errorResponse.setResponseCode(ResponseCodes.RECORD_NOT_EXIST_CODE.getResponseCode());
//                errorResponse.setResponseMessage(messageSource.getMessage("appMessages.user.notexist", new Object[]{requestBy}, Locale.ENGLISH));
//                return gson.toJson(errorResponse);
//            }
//
//            //Check if the Account exist in the DB
//            Account account = adminRepository.getAccountUsingAccountNumber(requestPayload.getAccountNumber());
//            if (account != null) {
//                //Fetch the customer record using the customer_id field
//                Customer customer = adminRepository.getCustomerUsingCustomerID(account.getCustomer().getId());
//                if (customer != null) {
//                    //Customer exist return record
//                    genericService.generateLog("Customer Upload", token, messageSource.getMessage("appMessages.customer.exist", new Object[]{" customer number " + customer.getCustomerNumber()}, Locale.ENGLISH), "API Request", "INFO", requestPayload.getRequestId());
//                    errorResponse.setResponseCode(ResponseCodes.RECORD_EXIST_CODE.getResponseCode());
//                    errorResponse.setResponseMessage(messageSource.getMessage("appMessages.customer.exist", new Object[]{" customer number " + customer.getCustomerNumber()}, Locale.ENGLISH));
//                    return gson.toJson(errorResponse);
//                } else {
//                    //Ftech customer record using the mobile number
//                    customer = adminRepository.getCustomerUsingMobileNumber(requestPayload.getMobileNumber());
//                    if (customer != null) {
//                        //Customer record exist return same
//                        genericService.generateLog("Customer Upload", token, messageSource.getMessage("appMessages.customer.exist", new Object[]{" mobile number " + requestPayload.getMobileNumber()}, Locale.ENGLISH), "API Request", "INFO", requestPayload.getRequestId());
//                        errorResponse.setResponseCode(ResponseCodes.RECORD_EXIST_CODE.getResponseCode());
//                        errorResponse.setResponseMessage(messageSource.getMessage("appMessages.customer.exist", new Object[]{" mobile number " + customer.getMobileNumber()}, Locale.ENGLISH));
//                        return gson.toJson(errorResponse);
//                    } else {
//                        //Fetch customer record from the CBA
//                        String branchCode = genericService.getBranchCode(requestPayload.getAccountNumber());
//                        Branch branch = genericService.getBranchUsingBranchCode(branchCode);
//                        String ofsRequest = accountVersion.trim().replace("/I/", "/S/") + "," + userCredentials
//                                + "/" + branchCode.trim() + "," + requestPayload.getAccountNumber();
//                        String newOfsRequest = genericService.formatOfsUserCredentials(ofsRequest, userCredentials);
//                        //Generate the OFS Response log
//                        genericService.generateLog("Customer Upload", token, newOfsRequest, "OFS Request", "INFO", requestPayload.getRequestId());
//                        //Post to T24
//                        response = genericService.postToT24(ofsRequest);
//                        //Generate the OFS Response log
//                        genericService.generateLog("Customer Upload", token, response, "OFS Response", "INFO", requestPayload.getRequestId());
//                        String validationResponse = genericService.validateT24Response(response);
//                        if (validationResponse != null) {
//                            errorResponse.setResponseCode(ResponseCodes.INTERNAL_SERVER_ERROR.getResponseCode());
//                            errorResponse.setResponseMessage(validationResponse);
//                            //Log the error
//                            genericService.generateLog("Customer Upload", token, validationResponse, "API Error", "DEBUG", requestPayload.getRequestId());
//                            return gson.toJson(errorResponse);
//                        }
//
//                        String customerNumber = genericService.getTextFromOFSResponse(response, "CUSTOMER:1:1");
//                        String accountOfficer = genericService.getTextFromOFSResponse(response, "ACCOUNT.OFFICER:1:1");
//                        String otherOfficer = genericService.getTextFromOFSResponse(response, "OTHER.OFFICER:1:1");
//                        ofsRequest = customerVersion.trim().replace("/I/", "/S/") + "," + userCredentials
//                                + "/" + branchCode.trim() + "," + customerNumber;
//                        newOfsRequest = genericService.formatOfsUserCredentials(ofsRequest, userCredentials);
//                        //Generate the OFS Response log
//                        genericService.generateLog("Customer Upload", token, newOfsRequest, "OFS Request", "INFO", requestPayload.getRequestId());
//                        //Post to T24
//                        response = genericService.postToT24(ofsRequest);
//                        //Generate the OFS Response log
//                        genericService.generateLog("Customer Upload", token, response, "OFS Response", "INFO", requestPayload.getRequestId());
//                        validationResponse = genericService.validateT24Response(response);
//                        if (validationResponse != null) {
//                            errorResponse.setResponseCode(ResponseCodes.INTERNAL_SERVER_ERROR.getResponseCode());
//                            errorResponse.setResponseMessage(validationResponse);
//                            //Log the error
//                            genericService.generateLog("Customer Upload", token, validationResponse, "API Error", "DEBUG", requestPayload.getRequestId());
//                            return gson.toJson(errorResponse);
//                        }
//
//                        //Check if there is record with the mobile Number
//                        customer = adminRepository.getCustomerUsingCustomerNumber(customerNumber);
//                        if (customer != null) {
//                            genericService.generateLog("Customer Upload", token, messageSource.getMessage("appMessages.customer.exist", new Object[]{" customer number " + customerNumber}, Locale.ENGLISH), "API Request", "INFO", requestPayload.getRequestId());
//                            errorResponse.setResponseCode(ResponseCodes.RECORD_EXIST_CODE.getResponseCode());
//                            errorResponse.setResponseMessage(messageSource.getMessage("appMessages.customer.exist", new Object[]{" customer number " + customer.getCustomerNumber()}, Locale.ENGLISH));
//                            return gson.toJson(errorResponse);
//                        } else {
//                            //Persist the record to the DB
//                            String lastName = genericService.getTextFromOFSResponse(response, "SHORT.NAME:1:1");
//                            String otherName = genericService.getTextFromOFSResponse(response, "NAME.1:1:1");
//                            String maritalStatus = genericService.getTextFromOFSResponse(response, "MARRIED.STATUS:1:1");
//                            String mnemonic = genericService.getTextFromOFSResponse(response, "MNEMONIC:1:1");
//                            String gender = genericService.getTextFromOFSResponse(response, "WORK.GEN:1:1");
//                            String kycTier = genericService.getTextFromOFSResponse(response, "KYC.LEVEL:1:1");
//                            String dob = genericService.formatDateWithHyphen(genericService.getTextFromOFSResponse(response, "BIRTH.INCORP.DATE:1:1"));
//                            String email = genericService.getTextFromOFSResponse(response, "CUSTOMER.NO:1:1");
//                            String address = genericService.getTextFromOFSResponse(response, "STREET:1:1") == null ? "" : genericService.getTextFromOFSResponse(response, "STREET:1:1")
//                                    + genericService.getTextFromOFSResponse(response, "STREET:2:1") == null ? "" : genericService.getTextFromOFSResponse(response, "STREET:2:1")
//                                    + genericService.getTextFromOFSResponse(response, "STREET:3:1") == null ? "" : genericService.getTextFromOFSResponse(response, "STREET:3:1")
//                                    + genericService.getTextFromOFSResponse(response, "STREET:4:1") == null ? "" : genericService.getTextFromOFSResponse(response, "STREET:4:1");
//                            String city = genericService.getTextFromOFSResponse(response, "SUBURB.TOWN:1:1");
//                            String state = genericService.getTextFromOFSResponse(response, "PROVINCE.STATE:1:1");
//                            String sector = genericService.getTextFromOFSResponse(response, "SECTOR:1:1");
//                            String title = genericService.getTextFromOFSResponse(response, "WORK.TITLE:1:1");
//                            String education = genericService.getTextFromOFSResponse(response, "PROFESSION:1:1");
//
//                            if (kycTier.equalsIgnoreCase("1")) {
//                                tierDeposit = tier1DepositLimit;
//                                tierWithdrawal = tier1WithdrawalLimit;
//                                tierBalance = tier1BalanceLimit;
//                                tierDailyBalance = tier1DailyBalance;
//                            } else if (kycTier.equalsIgnoreCase("2")) {
//                                tierDeposit = tier2DepositLimit;
//                                tierWithdrawal = tier2WithdrawalLimit;
//                                tierBalance = tier2BalanceLimit;
//                                tierDailyBalance = tier2DailyBalance;
//                            } else {
//                                tierDeposit = tier3DepositLimit;
//                                tierWithdrawal = tier3WithdrawalLimit;
//                                tierBalance = tier3BalanceLimit;
//                                tierDailyBalance = tier3DailyBalance;
//                            }
//
//                            Customer newCustomer = new Customer();
//                            newCustomer.setBranch(branch);
//                            newCustomer.setAccountOfficerCode(accountOfficer);
//                            newCustomer.setAppUser(appUser);
//                            newCustomer.setCreatedAt(LocalDateTime.now());
//                            newCustomer.setCustomerNumber(customerNumber);
//                            newCustomer.setCustomerType("INDIVIDUAL");
//                            newCustomer.setDob(LocalDate.parse(dob));
//                            newCustomer.setEmail(email == null ? "" : email);
//                            newCustomer.setEducationLevel(education);
//                            newCustomer.setGender(gender.equalsIgnoreCase("2") ? "FEMALE" : "MALE");
//                            newCustomer.setLastName(lastName);
//                            newCustomer.setKycTier(kycTier);
//                            newCustomer.setMaritalStatus(maritalStatus);
//                            newCustomer.setMnemonic(mnemonic);
//                            newCustomer.setMobileNumber(requestPayload.getMobileNumber());
//                            newCustomer.setOtherName(otherName);
//                            newCustomer.setOtherOfficerCode(otherOfficer);
//                            newCustomer.setResidenceAddress(address);
//                            newCustomer.setResidenceCity(city);
//                            newCustomer.setResidenceState(state);
//                            newCustomer.setSector(sector);
//                            newCustomer.setStatus("SUCCESS");
//                            newCustomer.setTitle(title);
//                            newCustomer.setBalanceLimit(tierBalance);
//                            newCustomer.setDepositLimit(tierDeposit);
//                            newCustomer.setDailyLimit(tierDailyBalance);
//                            newCustomer.setWithdrawalLimit(tierWithdrawal);
//                            newCustomer.setRequestId(requestPayload.getRequestId());
//                            newCustomer.setTimePeriod(genericService.getTimePeriod());
//                            String referalCode = UUID.randomUUID().toString().substring(0, 9);
//                            newCustomer.setReferalCode(referalCode);
//                            Customer customerRecord = adminRepository.createCustomer(newCustomer);
//
//                            customerResponse.setCustomerNumber(customerRecord.getCustomerNumber());
//                            customerResponse.setLastName(customerRecord.getLastName());
//                            customerResponse.setOtherName(customerRecord.getOtherName());
//                            customerResponse.setBranchCode(customerRecord.getBranch().getBranchCode());
//                            customerResponse.setMnemonic(customerRecord.getMnemonic());
//                            customerResponse.setMobileNumber(customerRecord.getMobileNumber());
//                            customerResponse.setDob(customerRecord.getDob().toString());
//                            customerResponse.setGender(customerRecord.getGender());
//                            customerResponse.setMaritalStatus(customerRecord.getMaritalStatus());
//                            customerResponse.setStateOfResidence(customerRecord.getResidenceState());
//                            customerResponse.setCityOfResidence(customerRecord.getResidenceCity());
//                            customerResponse.setResidentialAddress(customerRecord.getResidenceAddress());
//                            customerResponse.setKyc(customerRecord.getKycTier());
//                            customerResponse.setSecurityQuestion(customerRecord.getSecurityQuestion());
//                            customerResponse.setStatus(customerRecord.getStatus());
//                            customerResponse.setCustomerType(customerRecord.getCustomerType());
//                            customerResponse.setBoarded(customerRecord.isBoarded());
//                            customerResponse.setBVN(customerRecord.getBvn() == null ? "" : customerRecord.getBvn().getCustomerBvn());
//                            customerResponse.setResponseCode(ResponseCodes.SUCCESS_CODE.getResponseCode());
//                            customerResponse.setReferalCode(customerRecord.getReferalCode());
//                            return gson.toJson(customerResponse);
//                        }
//                    }
//                }
//            } else {
//                //Check the Customer record using the mobile number
//                Customer customer = adminRepository.getCustomerUsingMobileNumber(requestPayload.getMobileNumber());
//                if (customer != null) {
//                    //Customer record exist return same
//                    genericService.generateLog("Customer Upload", token, messageSource.getMessage("appMessages.customer.exist", new Object[]{" mobile number " + requestPayload.getMobileNumber()}, Locale.ENGLISH), "API Request", "INFO", requestPayload.getRequestId());
//                    errorResponse.setResponseCode(ResponseCodes.RECORD_EXIST_CODE.getResponseCode());
//                    errorResponse.setResponseMessage(messageSource.getMessage("appMessages.customer.exist", new Object[]{" customer number " + customer.getCustomerNumber()}, Locale.ENGLISH));
//                    return gson.toJson(errorResponse);
//                } else {
//                    //Fetch customer record from the CBA
//                    String branchCode = genericService.getBranchCode(requestPayload.getAccountNumber());
//                    Branch branch = genericService.getBranchUsingBranchCode(branchCode);
//                    String ofsRequest = accountVersion.trim().replace("/I/", "/S/") + "," + userCredentials
//                            + "/" + branchCode.trim() + "," + requestPayload.getAccountNumber();
//                    String newOfsRequest = genericService.formatOfsUserCredentials(ofsRequest, userCredentials);
//                    //Generate the OFS Response log
//                    genericService.generateLog("Customer Upload", token, newOfsRequest, "OFS Request", "INFO", requestPayload.getRequestId());
//                    //Post to T24
//                    response = genericService.postToT24(ofsRequest);
//                    //Generate the OFS Response log
//                    genericService.generateLog("Customer Upload", token, response, "OFS Response", "INFO", requestPayload.getRequestId());
//                    String validationResponse = genericService.validateT24Response(response);
//                    if (validationResponse != null) {
//                        errorResponse.setResponseCode(ResponseCodes.INTERNAL_SERVER_ERROR.getResponseCode());
//                        errorResponse.setResponseMessage(validationResponse);
//                        //Log the error
//                        genericService.generateLog("Customer Upload", token, validationResponse, "API Error", "DEBUG", requestPayload.getRequestId());
//                        return gson.toJson(errorResponse);
//                    }
//
//                    String customerNumber = genericService.getTextFromOFSResponse(response, "CUSTOMER:1:1");
//                    String accountOfficer = genericService.getTextFromOFSResponse(response, "ACCOUNT.OFFICER:1:1");
//                    String otherOfficer = genericService.getTextFromOFSResponse(response, "OTHER.OFFICER:1:1");
//                    ofsRequest = customerVersion.trim().replace("/I/", "/S/") + "," + userCredentials
//                            + "/" + branchCode.trim() + "," + customerNumber;
//                    newOfsRequest = genericService.formatOfsUserCredentials(ofsRequest, userCredentials);
//                    //Generate the OFS Response log
//                    genericService.generateLog("Customer Upload", token, newOfsRequest, "OFS Request", "INFO", requestPayload.getRequestId());
//                    //Post to T24
//                    response = genericService.postToT24(ofsRequest);
//                    //Generate the OFS Response log
//                    genericService.generateLog("Customer Upload", token, response, "OFS Response", "INFO", requestPayload.getRequestId());
//                    validationResponse = genericService.validateT24Response(response);
//                    if (validationResponse != null) {
//                        errorResponse.setResponseCode(ResponseCodes.INTERNAL_SERVER_ERROR.getResponseCode());
//                        errorResponse.setResponseMessage(validationResponse);
//                        //Log the error
//                        genericService.generateLog("Customer Upload", token, validationResponse, "API Error", "DEBUG", requestPayload.getRequestId());
//                        return gson.toJson(errorResponse);
//                    }
//
//                    //Check if there is record with the mobile Number
//                    customer = adminRepository.getCustomerUsingCustomerNumber(customerNumber);
//                    if (customer != null) {
//                        genericService.generateLog("Customer Upload", token, messageSource.getMessage("appMessages.customer.exist", new Object[]{" customer number " + customerNumber}, Locale.ENGLISH), "API Request", "INFO", requestPayload.getRequestId());
//                        errorResponse.setResponseCode(ResponseCodes.RECORD_EXIST_CODE.getResponseCode());
//                        errorResponse.setResponseMessage(messageSource.getMessage("appMessages.customer.exist", new Object[]{" customer number " + customer.getCustomerNumber()}, Locale.ENGLISH));
//                        return gson.toJson(errorResponse);
//                    } else {
//                        //Persist the record to the DB
//                        String lastName = genericService.getTextFromOFSResponse(response, "SHORT.NAME:1:1");
//                        String otherName = genericService.getTextFromOFSResponse(response, "NAME.1:1:1");
//                        String maritalStatus = genericService.getTextFromOFSResponse(response, "MARRIED.STATUS:1:1");
//                        String mnemonic = genericService.getTextFromOFSResponse(response, "MNEMONIC:1:1");
//                        String gender = genericService.getTextFromOFSResponse(response, "WORK.GEN:1:1");
//                        String kycTier = genericService.getTextFromOFSResponse(response, "KYC.LEVEL:1:1");
//                        String dob = genericService.formatDateWithHyphen(genericService.getTextFromOFSResponse(response, "BIRTH.INCORP.DATE:1:1"));
//                        String email = genericService.getTextFromOFSResponse(response, "CUSTOMER.NO:1:1");
//                        String address = genericService.getTextFromOFSResponse(response, "STREET:1:1") == null ? "" : genericService.getTextFromOFSResponse(response, "STREET:1:1")
//                                + genericService.getTextFromOFSResponse(response, "STREET:2:1") == null ? "" : genericService.getTextFromOFSResponse(response, "STREET:2:1")
//                                + genericService.getTextFromOFSResponse(response, "STREET:3:1") == null ? "" : genericService.getTextFromOFSResponse(response, "STREET:3:1")
//                                + genericService.getTextFromOFSResponse(response, "STREET:4:1") == null ? "" : genericService.getTextFromOFSResponse(response, "STREET:4:1");
//                        String city = genericService.getTextFromOFSResponse(response, "SUBURB.TOWN:1:1");
//                        String state = genericService.getTextFromOFSResponse(response, "PROVINCE.STATE:1:1");
//                        String sector = genericService.getTextFromOFSResponse(response, "SECTOR:1:1");
//                        String title = genericService.getTextFromOFSResponse(response, "WORK.TITLE:1:1");
//                        String education = genericService.getTextFromOFSResponse(response, "PROFESSION:1:1");
//
//                        if (kycTier.equalsIgnoreCase("1")) {
//                            tierDeposit = tier1DepositLimit;
//                            tierWithdrawal = tier1WithdrawalLimit;
//                            tierBalance = tier1BalanceLimit;
//                            tierDailyBalance = tier1DailyBalance;
//                        } else if (kycTier.equalsIgnoreCase("2")) {
//                            tierDeposit = tier2DepositLimit;
//                            tierWithdrawal = tier2WithdrawalLimit;
//                            tierBalance = tier2BalanceLimit;
//                            tierDailyBalance = tier2DailyBalance;
//                        } else {
//                            tierDeposit = tier3DepositLimit;
//                            tierWithdrawal = tier3WithdrawalLimit;
//                            tierBalance = tier3BalanceLimit;
//                            tierDailyBalance = tier3DailyBalance;
//                        }
//
//                        Customer newCustomer = new Customer();
//                        newCustomer.setBranch(branch);
//                        newCustomer.setAccountOfficerCode(accountOfficer);
//                        newCustomer.setAppUser(appUser);
//                        newCustomer.setCreatedAt(LocalDateTime.now());
//                        newCustomer.setCustomerNumber(customerNumber);
//                        newCustomer.setCustomerType("INDIVIDUAL");
//                        newCustomer.setDob(LocalDate.parse(dob));
//                        newCustomer.setEmail(email == null ? "" : email);
//                        newCustomer.setEducationLevel(education);
//                        newCustomer.setGender(gender.equalsIgnoreCase("2") ? "FEMALE" : "MALE");
//                        newCustomer.setLastName(lastName);
//                        newCustomer.setKycTier(kycTier);
//                        newCustomer.setMaritalStatus(maritalStatus);
//                        newCustomer.setMnemonic(mnemonic);
//                        newCustomer.setMobileNumber(requestPayload.getMobileNumber());
//                        newCustomer.setOtherName(otherName);
//                        newCustomer.setOtherOfficerCode(otherOfficer);
//                        newCustomer.setResidenceAddress(address);
//                        newCustomer.setResidenceCity(city);
//                        newCustomer.setResidenceState(state);
//                        newCustomer.setSector(sector);
//                        newCustomer.setStatus("SUCCESS");
//                        newCustomer.setTitle(title);
//                        newCustomer.setBalanceLimit(tierBalance);
//                        newCustomer.setDepositLimit(tierDeposit);
//                        newCustomer.setDailyLimit(tierDailyBalance);
//                        newCustomer.setWithdrawalLimit(tierWithdrawal);
//                        newCustomer.setRequestId(requestPayload.getRequestId());
//                        newCustomer.setTimePeriod(genericService.getTimePeriod());
//                        String referalCode = UUID.randomUUID().toString().substring(0, 9);
//                        newCustomer.setReferalCode(referalCode);
//                        Customer customerRecord = adminRepository.createCustomer(newCustomer);
//
//                        customerResponse.setCustomerNumber(customerRecord.getCustomerNumber());
//                        customerResponse.setLastName(customerRecord.getLastName());
//                        customerResponse.setOtherName(customerRecord.getOtherName());
//                        customerResponse.setBranchCode(customerRecord.getBranch().getBranchCode());
//                        customerResponse.setMnemonic(customerRecord.getMnemonic());
//                        customerResponse.setMobileNumber(customerRecord.getMobileNumber());
//                        customerResponse.setDob(customerRecord.getDob().toString());
//                        customerResponse.setGender(customerRecord.getGender());
//                        customerResponse.setMaritalStatus(customerRecord.getMaritalStatus());
//                        customerResponse.setStateOfResidence(customerRecord.getResidenceState());
//                        customerResponse.setCityOfResidence(customerRecord.getResidenceCity());
//                        customerResponse.setResidentialAddress(customerRecord.getResidenceAddress());
//                        customerResponse.setKyc(customerRecord.getKycTier());
//                        customerResponse.setSecurityQuestion(customerRecord.getSecurityQuestion());
//                        customerResponse.setStatus(customerRecord.getStatus());
//                        customerResponse.setCustomerType(customerRecord.getCustomerType());
//                        customerResponse.setBoarded(customerRecord.isBoarded());
//                        customerResponse.setBVN(customerRecord.getBvn() == null ? "" : customerRecord.getBvn().getCustomerBvn());
//                        customerResponse.setResponseCode(ResponseCodes.SUCCESS_CODE.getResponseCode());
//                        customerResponse.setReferalCode(customerRecord.getReferalCode());
//                        return gson.toJson(customerResponse);
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            genericService.generateLog("Customer Upload", token, ex.getMessage(), "API Request", "INFO", requestPayload.getRequestId());
//            errorResponse.setResponseCode(ResponseCodes.INTERNAL_SERVER_ERROR.getResponseCode());
//            errorResponse.setResponseMessage(ex.getMessage());
//            return gson.toJson(errorResponse);
//        }
//    }
//
//    @Override
//    public String uploadAccountRecord(String token, AccountUploadRequestPayload requestPayload) {
//        String requestBy = jwtToken.getUserNameFromToken(token);
//        String userCredentials = jwtToken.getUserCredentialFromToken(token);
//        OmniResponsePayload errorResponse = new OmniResponsePayload();
//        //Create request log
//        String requestJson = gson.toJson(requestPayload);
//        genericService.generateLog("Account Upload", token, requestJson, "API Request", "INFO", requestPayload.getRequestId());
//        String response = "";
//        try {
//            //Check the channel information
//            AppUser appUser = adminRepository.getAppUserUsingUsername(requestBy);
//            if (appUser == null) {
//                //Log the error
//                genericService.generateLog("Account Upload", token, messageSource.getMessage("appMessages.user.notexist", new Object[]{requestBy}, Locale.ENGLISH), "API Response", "INFO", requestPayload.getRequestId());
//                errorResponse.setResponseCode(ResponseCodes.RECORD_NOT_EXIST_CODE.getResponseCode());
//                errorResponse.setResponseMessage(messageSource.getMessage("appMessages.user.notexist", new Object[]{requestBy}, Locale.ENGLISH));
//                return gson.toJson(errorResponse);
//            }
//
//            //Check if the Account exist in the DB
//            Account account = adminRepository.getAccountUsingAccountNumber(requestPayload.getAccountNumber());
//            if (account != null) {
//                //Account exist return record
//                genericService.generateLog("Account Upload", token, messageSource.getMessage("appMessages.account.exist", new Object[]{requestPayload.getAccountNumber()}, Locale.ENGLISH), "API Request", "INFO", requestPayload.getRequestId());
//                errorResponse.setResponseCode(ResponseCodes.RECORD_EXIST_CODE.getResponseCode());
//                errorResponse.setResponseMessage(messageSource.getMessage("appMessages.account.exist", new Object[]{requestPayload.getAccountNumber()}, Locale.ENGLISH));
//                return gson.toJson(errorResponse);
//            }
//
//            //Fetch customer record from the CBA
//            String branchCode = genericService.getBranchCode(requestPayload.getAccountNumber());
//            Branch branch = genericService.getBranchUsingBranchCode(branchCode);
//            String ofsRequest = accountVersion.trim().replace("/I/", "/S/") + "," + userCredentials
//                    + "/" + branchCode.trim() + "," + requestPayload.getAccountNumber();
//            String newOfsRequest = genericService.formatOfsUserCredentials(ofsRequest, userCredentials);
//            //Generate the OFS Response log
//            genericService.generateLog("Account Upload", token, newOfsRequest, "OFS Request", "INFO", requestPayload.getRequestId());
//            //Post to T24
//            response = genericService.postToT24(ofsRequest);
//            //Generate the OFS Response log
//            genericService.generateLog("Account Upload", token, response, "OFS Response", "INFO", requestPayload.getRequestId());
//            String validationResponse = genericService.validateT24Response(response);
//            if (validationResponse != null) {
//                errorResponse.setResponseCode(ResponseCodes.INTERNAL_SERVER_ERROR.getResponseCode());
//                errorResponse.setResponseMessage(validationResponse);
//                //Log the error
//                genericService.generateLog("Account Upload", token, validationResponse, "API Error", "DEBUG", requestPayload.getRequestId());
//                return gson.toJson(errorResponse);
//            }
//
//            String customerNumber = genericService.getTextFromOFSResponse(response, "CUSTOMER:1:1");
//            String oldAccountNumber = genericService.getT24TransIdFromResponse(response);
//            String nubanAccountNumber = genericService.getTextFromOFSResponse(response, "ALT.ACCT.ID:4:1");
//            String category = genericService.getTextFromOFSResponse(response, "CATEGORY:1:1");
//
//            //Check if the Customer exist with the customer number
//            Customer customer = adminRepository.getCustomerUsingCustomerNumber(customerNumber);
//            if (customer == null) {
//                genericService.generateLog("Account Upload", token, messageSource.getMessage("appMessages.customer.notexist", new Object[]{" customer number " + customerNumber}, Locale.ENGLISH), "API Request", "INFO", requestPayload.getRequestId());
//                errorResponse.setResponseCode(ResponseCodes.RECORD_NOT_EXIST_CODE.getResponseCode());
//                errorResponse.setResponseMessage(messageSource.getMessage("appMessages.customer.notexist", new Object[]{" customer number " + customerNumber}, Locale.ENGLISH));
//                return gson.toJson(errorResponse);
//            }
//
//            Product product = adminRepository.getProductUsingProductCode(category.trim());
//            if (product == null) {
//                //Log the error
//                genericService.generateLog("Account Upload", token, messageSource.getMessage("appMessages.record.product.noexist", new Object[]{category}, Locale.ENGLISH), "API Error", "DEBUG", requestPayload.getRequestId());
//                errorResponse.setResponseCode(ResponseCodes.RECORD_NOT_EXIST_CODE.getResponseCode());
//                errorResponse.setResponseMessage(messageSource.getMessage("appMessages.record.product.noexist", new Object[]{category}, Locale.ENGLISH));
//                return gson.toJson(errorResponse);
//            }
//
//            Account newAccount = new Account();
//            newAccount.setAccountBalance(BigDecimal.ZERO);
//            newAccount.setAccountNumber(nubanAccountNumber);
//            newAccount.setAppUser(appUser);
//            newAccount.setBranch(branch);
//            newAccount.setCategory(category);
//            newAccount.setCreatedAt(LocalDateTime.now());
//            newAccount.setCustomer(customer);
//            newAccount.setAlternateAccountNumber(oldAccountNumber);
//            newAccount.setOpenedWithBVN(false);
//            newAccount.setProduct(product);
//            newAccount.setRequestId(requestPayload.getRequestId());
//            newAccount.setStatus("SUCCESS");
//            newAccount.setTimePeriod(genericService.getTimePeriod());
//            newAccount.setWallet(false);
//            adminRepository.createAccount(newAccount);
//
//            //Generate the response
//            AccountDetailsResponsePayload accountResponse = new AccountDetailsResponsePayload();
//            accountResponse.setAccountName(customer.getLastName() + " " + customer.getOtherName());
//            accountResponse.setAccountNumber(nubanAccountNumber);
//            accountResponse.setBranch(customer.getBranch().getBranchName());
//            accountResponse.setBranchCode(customer.getBranch().getBranchCode());
//            accountResponse.setCategory(product.getCategoryCode());
//            accountResponse.setCustomerNumber(customer.getCustomerNumber());
//            accountResponse.setOpenedWithBVN(false);
//            accountResponse.setProductCode(product.getProductCode());
//            accountResponse.setProductName(product.getProductName());
//            accountResponse.setWallet(false);
//            accountResponse.setResponseCode(ResponseCodes.SUCCESS_CODE.getResponseCode());
//            return gson.toJson(accountResponse);
//        } catch (Exception ex) {
//            genericService.generateLog("Account Upload", token, ex.getMessage(), "API Request", "INFO", requestPayload.getRequestId());
//            errorResponse.setResponseCode(ResponseCodes.INTERNAL_SERVER_ERROR.getResponseCode());
//            errorResponse.setResponseMessage(ex.getMessage());
//            return gson.toJson(errorResponse);
//        }
//    }
//
//    @Override
//    public String reverseTransaction(String token, TransactionRequestPayload requestPayload) {
//        String requestBy = jwtToken.getUserNameFromToken(token);
//        String userCredentials = jwtToken.getUserCredentialFromToken(token);
//        OmniResponsePayload errorResponse = new OmniResponsePayload();
//        //Create request log
//        String requestJson = gson.toJson(requestPayload);
//        genericService.generateLog("Transaction Reversal", token, requestJson, "API Request", "INFO", requestPayload.getRequestId());
//        String response = "";
//        try {
//            //Check the channel information
//            AppUser appUser = adminRepository.getAppUserUsingUsername(requestBy);
//            if (appUser == null) {
//                //Log the error
//                genericService.generateLog("Transaction Reversal", token, messageSource.getMessage("appMessages.user.notexist", new Object[]{requestBy}, Locale.ENGLISH), "API Response", "INFO", requestPayload.getRequestId());
//                errorResponse.setResponseCode(ResponseCodes.RECORD_NOT_EXIST_CODE.getResponseCode());
//                errorResponse.setResponseMessage(messageSource.getMessage("appMessages.user.notexist", new Object[]{requestBy}, Locale.ENGLISH));
//                return gson.toJson(errorResponse);
//            }
//
//            switch (requestPayload.getTransType()) {
//                case "AIRTIME": {
//                    //Check if the trasaction reference exist in the DB
//                    Airtime airtimeRecord = adminRepository.getAirtimeUsingTransRef(requestPayload.getTransRef());
//                    if (airtimeRecord == null) {
//                        //Log the error
//                        genericService.generateLog("Transaction Reversal", token, messageSource.getMessage("appMessages.record.airtime.notexist", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH), "API Response", "INFO", requestPayload.getRequestId());
//                        errorResponse.setResponseCode(ResponseCodes.RECORD_NOT_EXIST_CODE.getResponseCode());
//                        errorResponse.setResponseMessage(messageSource.getMessage("appMessages.record.airtime.notexist", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH));
//                        return gson.toJson(errorResponse);
//                    }
//
//                    //Check the status of the transaction
//                    if (airtimeRecord.getStatus().equalsIgnoreCase("SUCCESS")) {
//                        //Log the error
//                        genericService.generateLog("Transaction Reversal", token, messageSource.getMessage("appMessages.trans.reversed", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH), "API Response", "INFO", requestPayload.getRequestId());
//                        errorResponse.setResponseCode(ResponseCodes.IRREVERSIBLE_TRANSACTION.getResponseCode());
//                        errorResponse.setResponseMessage(messageSource.getMessage("appMessages.trans.reversed", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH));
//                        return gson.toJson(errorResponse);
//                    }
//
//                    //Check if the transaction was successful at XpressPay
//                    XpressPayTransactionQueryResponsePayload statusResponse = transactionQuery(airtimeRecord.getTransRefToBiller());
//                    if (statusResponse.getResponseCode().equals("00")) {
//                        //Log the error
//                        genericService.generateLog("Transaction Reversal", token, messageSource.getMessage("appMessages.trans.biller.success", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH), "API Response", "INFO", requestPayload.getRequestId());
//                        errorResponse.setResponseCode(ResponseCodes.IRREVERSIBLE_TRANSACTION.getResponseCode());
//                        errorResponse.setResponseMessage(messageSource.getMessage("appMessages.trans.biller.success", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH));
//                        return gson.toJson(errorResponse);
//                    }
//
//                    if (!statusResponse.getResponseCode().equals("00")) {
//                        if (airtimeRecord.isCustomerDebited()) {
//                            //Reverse the transaction
//                            String ofsRequest = reveresalVersion.trim() + "," + userCredentials
//                                    + "/" + digitalBranchCode.trim() + "," + airtimeRecord.getT24TransRef();
//                            String newOfsRequest = genericService.formatOfsUserCredentials(ofsRequest, userCredentials);
//                            //Generate the OFS Request log
//                            genericService.generateLog(String.valueOf(airtimeRecord.getAirtimeFor()), token, newOfsRequest, "OFS Request", "INFO", requestPayload.getRequestId());
//                            //Post to T24
//
//                            response = genericService.postToT24(ofsRequest);
//                            //Generate the OFS Request log
//                            genericService.generateLog(String.valueOf(airtimeRecord.getAirtimeFor()), token, response, "OFS Response", "INFO", requestPayload.getRequestId());
//                            String validationResponse = genericService.validateT24Response(response);
//                            if (validationResponse != null) {
//                                //Log the error
//                                genericService.generateLog("Transaction Reversal", token, messageSource.getMessage("appMessages.trans.biller.success", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH), "API Response", "INFO", requestPayload.getRequestId());
//                                errorResponse.setResponseCode(ResponseCodes.FAILED_TRANSACTION.getResponseCode());
//                                errorResponse.setResponseMessage(messageSource.getMessage("appMessages.trans.biller.success", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH));
//                                return gson.toJson(errorResponse);
//                            }
//
//                            airtimeRecord.setT24ReverseTransRef(genericService.getT24TransIdFromResponse(response));
//                            airtimeRecord.setStatus("REVERSED");
//                            adminRepository.updateAirtime(airtimeRecord);
//                        }
//
//                        //Customer was not debited before
//                        genericService.generateLog("Transaction Reversal", token, messageSource.getMessage("appMessages.trans.no.debit", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH), "API Response", "INFO", requestPayload.getRequestId());
//                        errorResponse.setResponseCode(ResponseCodes.FAILED_TRANSACTION.getResponseCode());
//                        errorResponse.setResponseMessage(messageSource.getMessage("appMessages.trans.no.debit", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH));
//                        return gson.toJson(errorResponse);
//                    }
//                    break;
//                }
//                case "CABLE TV": {
//                    //Check if the trasaction reference exist in the DB
//                    CableTVSubscription cableTVRecord = adminRepository.getCableTVUsingTransRef(requestPayload.getTransRef());
//                    if (cableTVRecord == null) {
//                        //Log the error
//                        genericService.generateLog("Transaction Reversal", token, messageSource.getMessage("appMessages.record.cabletv.notexist", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH), "API Response", "INFO", requestPayload.getRequestId());
//                        errorResponse.setResponseCode(ResponseCodes.RECORD_NOT_EXIST_CODE.getResponseCode());
//                        errorResponse.setResponseMessage(messageSource.getMessage("appMessages.record.cabletv.notexist", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH));
//                        return gson.toJson(errorResponse);
//                    }
//
//                    //Check the status of the transaction
//                    if (cableTVRecord.getStatus().equalsIgnoreCase("SUCCESS")) {
//                        //Log the error
//                        genericService.generateLog("Transaction Reversal", token, messageSource.getMessage("appMessages.trans.reversed", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH), "API Response", "INFO", requestPayload.getRequestId());
//                        errorResponse.setResponseCode(ResponseCodes.IRREVERSIBLE_TRANSACTION.getResponseCode());
//                        errorResponse.setResponseMessage(messageSource.getMessage("appMessages.trans.reversed", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH));
//                        return gson.toJson(errorResponse);
//                    }
//
//                    //Check if the transaction was successful at XpressPay
//                    XpressPayTransactionQueryResponsePayload statusResponse = transactionQuery(cableTVRecord.getTransRefToBiller());
//                    if (statusResponse.getResponseCode().equals("00")) {
//                        //Log the error
//                        genericService.generateLog("Transaction Reversal", token, messageSource.getMessage("appMessages.trans.biller.success", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH), "API Response", "INFO", requestPayload.getRequestId());
//                        errorResponse.setResponseCode(ResponseCodes.IRREVERSIBLE_TRANSACTION.getResponseCode());
//                        errorResponse.setResponseMessage(messageSource.getMessage("appMessages.trans.biller.success", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH));
//                        return gson.toJson(errorResponse);
//                    }
//
//                    if (!statusResponse.getResponseCode().equals("00")) {
//                        if (cableTVRecord.isCustomerDebited()) {
//                            //Reverse the transaction
//                            String ofsRequest = reveresalVersion.trim() + "," + userCredentials
//                                    + "/" + digitalBranchCode.trim() + "," + cableTVRecord.getT24TransRef();
//                            String newOfsRequest = genericService.formatOfsUserCredentials(ofsRequest, userCredentials);
//                            //Generate the OFS Request log
//                            genericService.generateLog("Cable TV", token, newOfsRequest, "OFS Response", "INFO", requestPayload.getRequestId());
//                            //Post to T24
//                            response = genericService.postToT24(ofsRequest);
//                            //Generate the OFS Request log
//                            genericService.generateLog("Cable TV", token, response, "API Response", "DEBUG", requestPayload.getRequestId());
//                            String validationResponse = genericService.validateT24Response(response);
//                            if (validationResponse != null) {
//                                //Log the error
//                                genericService.generateLog("Transaction Reversal", token, messageSource.getMessage("appMessages.trans.biller.success", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH), "API Response", "INFO", requestPayload.getRequestId());
//                                errorResponse.setResponseCode(ResponseCodes.FAILED_TRANSACTION.getResponseCode());
//                                errorResponse.setResponseMessage(messageSource.getMessage("appMessages.trans.biller.success", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH));
//                                return gson.toJson(errorResponse);
//                            }
//
//                            cableTVRecord.setT24ReverseTransRef(genericService.getT24TransIdFromResponse(response));
//                            cableTVRecord.setStatus("REVERSED");
//                            adminRepository.updateCableTV(cableTVRecord);
//                        }
//
//                        //Customer was not debited before
//                        genericService.generateLog("Transaction Reversal", token, messageSource.getMessage("appMessages.trans.no.debit", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH), "API Response", "INFO", requestPayload.getRequestId());
//                        errorResponse.setResponseCode(ResponseCodes.FAILED_TRANSACTION.getResponseCode());
//                        errorResponse.setResponseMessage(messageSource.getMessage("appMessages.trans.no.debit", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH));
//                        return gson.toJson(errorResponse);
//                    }
//                    break;
//                }
//                case "ELECTRICITY": {
//                    //Check if the trasaction reference exist in the DB
//                    ElectricitySubscription electricityRecord = adminRepository.getElectricityUsingTransRef(requestPayload.getTransRef());
//                    if (electricityRecord == null) {
//                        //Log the error
//                        genericService.generateLog("Transaction Reversal", token, messageSource.getMessage("appMessages.record.electricity.notexist", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH), "API Response", "INFO", requestPayload.getRequestId());
//                        errorResponse.setResponseCode(ResponseCodes.RECORD_NOT_EXIST_CODE.getResponseCode());
//                        errorResponse.setResponseMessage(messageSource.getMessage("appMessages.record.electricity.notexist", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH));
//                        return gson.toJson(errorResponse);
//                    }
//
//                    //Check the status of the transaction
//                    if (electricityRecord.getStatus().equalsIgnoreCase("SUCCESS")) {
//                        //Log the error
//                        genericService.generateLog("Transaction Reversal", token, messageSource.getMessage("appMessages.trans.reversed", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH), "API Response", "INFO", requestPayload.getRequestId());
//                        errorResponse.setResponseCode(ResponseCodes.IRREVERSIBLE_TRANSACTION.getResponseCode());
//                        errorResponse.setResponseMessage(messageSource.getMessage("appMessages.trans.reversed", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH));
//                        return gson.toJson(errorResponse);
//                    }
//
//                    //Check if the transaction was successful at XpressPay
//                    XpressPayTransactionQueryResponsePayload statusResponse = transactionQuery(electricityRecord.getTransRefToBiller());
//                    if (statusResponse.getResponseCode().equals("00")) {
//                        //Log the error
//                        genericService.generateLog("Transaction Reversal", token, messageSource.getMessage("appMessages.trans.biller.success", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH), "API Response", "INFO", requestPayload.getRequestId());
//                        errorResponse.setResponseCode(ResponseCodes.IRREVERSIBLE_TRANSACTION.getResponseCode());
//                        errorResponse.setResponseMessage(messageSource.getMessage("appMessages.trans.biller.success", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH));
//                        return gson.toJson(errorResponse);
//                    }
//
//                    if (!statusResponse.getResponseCode().equals("00")) {
//                        if (electricityRecord.isCustomerDebited()) {
//                            //Reverse the transaction
//                            String ofsRequest = reveresalVersion.trim() + "," + userCredentials
//                                    + "/" + digitalBranchCode.trim() + "," + electricityRecord.getT24TransRef();
//                            String newOfsRequest = genericService.formatOfsUserCredentials(ofsRequest, userCredentials);
//                            //Generate the OFS Request log
//                            genericService.generateLog("Electricity", token, newOfsRequest, "OFS Request", "INFO", requestPayload.getRequestId());
//                            //Post to T24
//
//                            response = genericService.postToT24(ofsRequest);
//                            //Generate the OFS Request log
//                            genericService.generateLog("Electricity", token, response, "OFS Response", "INFO", requestPayload.getRequestId());
//                            String validationResponse = genericService.validateT24Response(response);
//                            if (validationResponse != null) {
//                                //Log the error
//                                genericService.generateLog("Transaction Reversal", token, messageSource.getMessage("appMessages.trans.biller.success", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH), "API Response", "INFO", requestPayload.getRequestId());
//                                errorResponse.setResponseCode(ResponseCodes.FAILED_TRANSACTION.getResponseCode());
//                                errorResponse.setResponseMessage(messageSource.getMessage("appMessages.trans.biller.success", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH));
//                                return gson.toJson(errorResponse);
//                            }
//
//                            electricityRecord.setT24ReverseTransRef(genericService.getT24TransIdFromResponse(response));
//                            electricityRecord.setStatus("REVERSED");
//                            adminRepository.updateElectricity(electricityRecord);
//                        }
//
//                        //Customer was not debited before
//                        genericService.generateLog("Transaction Reversal", token, messageSource.getMessage("appMessages.trans.no.debit", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH), "API Response", "INFO", requestPayload.getRequestId());
//                        errorResponse.setResponseCode(ResponseCodes.FAILED_TRANSACTION.getResponseCode());
//                        errorResponse.setResponseMessage(messageSource.getMessage("appMessages.trans.no.debit", new Object[]{requestPayload.getTransRef()}, Locale.ENGLISH));
//                        return gson.toJson(errorResponse);
//                    }
//                    break;
//                }
//            }
//
//            //Unknown transaction type
//            genericService.generateLog("Transaction Reversal", token, messageSource.getMessage("appMessages.trans.unknown", new Object[]{requestPayload.getTransType()}, Locale.ENGLISH), "API Response", "INFO", requestPayload.getRequestId());
//            errorResponse.setResponseCode(ResponseCodes.INVALID_TYPE.getResponseCode());
//            errorResponse.setResponseMessage(messageSource.getMessage("appMessages.trans.unknown", new Object[]{requestPayload.getTransType()}, Locale.ENGLISH));
//            return gson.toJson(errorResponse);
//        } catch (Exception ex) {
//            genericService.generateLog("Transaction Reversal", token, ex.getMessage(), "API Request", "INFO", requestPayload.getRequestId());
//            errorResponse.setResponseCode(ResponseCodes.INTERNAL_SERVER_ERROR.getResponseCode());
//            errorResponse.setResponseMessage(ex.getMessage());
//            return gson.toJson(errorResponse);
//        }
//    }
//
//    private String callXpressPayAPI(String url, String requestBody) {
//        try {
//            Unirest.setTimeouts(0, 0);
//            HttpResponse<String> httpResponse = Unirest.post(url)
//                    .header("Authorization", AUTHORIZATION)
//                    .header("webkey", WEB_KEY_LIVE)
//                    .header("accountid", ACCOUNT_ID)
//                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
//                    .body(requestBody)
//                    .asString();
//            logger.info("Xpress Pay Request - Airtime Data " + requestBody);
//            //Log the error
//            logger.info("Xpress Pay Response - Airtime Data " + httpResponse.getBody());
//            return httpResponse.getBody();
//        } catch (Exception ex) {
//            logger.info("Xpress Pay Response - Airtime Data " + ex.getMessage());
//            return ex.getMessage();
//        }
//    }

//    private XpressPayTransactionQueryResponsePayload transactionQuery(String transRef) {
//        //Do a transaction query to determine status before reversing
//        XpressPayTransactionQueryRequestPayload transQueryPayload = new XpressPayTransactionQueryRequestPayload();
//        transQueryPayload.setExternalRef(transRef);
//        String transQuery = gson.toJson(transQueryPayload);
//        String xpressPayResponse = callXpressPayAPI(XPRESSPAY_TRANS_QUERY_URL, transQuery);
//        XpressPayTransactionQueryResponsePayload transStatusResponse = gson.fromJson(xpressPayResponse, XpressPayTransactionQueryResponsePayload.class);
//        return transStatusResponse;
//    }
}
