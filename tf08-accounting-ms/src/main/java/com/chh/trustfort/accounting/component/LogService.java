package com.chh.trustfort.accounting.component;

import com.chh.trustfort.accounting.model.AppUser;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author dofoleta
 */
@Component
public class LogService {

    Logger logger = LoggerFactory.getLogger(LogService.class);


    public void logInfo(AppUser appUser, String logMessage, String logType, String requestId) {
        logger.info(prepareLog(appUser, logMessage, logType, requestId));
    }


    public void logError(AppUser appUser, String logMessage, String logType, String requestId) {
        logger.error(prepareLog(appUser, logMessage, logType, requestId));
    }

    private String prepareLog(AppUser appUser, String logMessage, String logType, String requestId) {
        try {
            String requestBy = appUser.getUserName();
            String remoteIP = appUser.getIpAddress();
            String channel = appUser.getChannel();

            StringBuilder strBuilder = new StringBuilder();
            strBuilder.append(logType.toUpperCase(Locale.ENGLISH));
            strBuilder.append(" - ");
            strBuilder.append("[").append(remoteIP).append(":").append(channel.toUpperCase(Locale.ENGLISH)).append(":").append(requestBy.toUpperCase(Locale.ENGLISH)).append("]");
            strBuilder.append("[").append(channel.toUpperCase(Locale.ENGLISH).toUpperCase(Locale.ENGLISH)).append(":").append(requestId.toUpperCase(Locale.ENGLISH)).append("]");
            strBuilder.append("[").append(logMessage).append("]");

            return strBuilder.toString();

        } catch (Exception ex) {
            if (logger.isDebugEnabled()) {
                logger.debug(ex.getMessage());
            }
        }
        return "";
    }

//    public void createUserActivity(String accountNumber, String activity, String amount, String channel, String message, String mobileNumber, char status) {
//        UserActivity newActivity = new UserActivity();
//        newActivity.setCustomerId(accountNumber);
//        newActivity.setActivity(activity);
//        newActivity.setAmount(amount);
//        newActivity.setChannel(channel);
//        newActivity.setCreatedAt(LocalDateTime.now());
//        newActivity.setMessage(message);
//        newActivity.setMobileNumber(mobileNumber);
//        newActivity.setStatus(status);
//        customerRepository.createUserActivity(newActivity);
//    }

}
