package com.chh.trustfort.payment.component;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;

/**
 *
 * @author Daniel Ofoleta
 */
@Component
public class CronJobs {

    @Autowired
    private ApplicationContext context;

//    @Value("${cron}")
//    private boolean runCronJob;

    @Value("${cron:true}")  // Default value is 'true' if not defined
    private boolean runCronJob;


    @Scheduled(fixedDelay = 30000, initialDelay = 1000)
    public void processscheduledTasks() {
        processNewCustomer();
        performIntegrityChecks();
    }

    private void processNewCustomer() {
        if (runCronJob) {
            try {
                Gson gson = context.getBean(Gson.class);

 
            } catch (JsonSyntaxException ex) {
            }
        }
    }



    private void performIntegrityChecks() {

    }

    private String formatMobileNumber(String mobileNumber) {
        if (mobileNumber != null && mobileNumber.startsWith("234")) {
            mobileNumber = mobileNumber.replace(mobileNumber.substring(0, 3), "0");
        }
        return mobileNumber;
    }

    private String nullToEmpty(String val) {
        if (val == null || val.isEmpty() || val.trim().isEmpty()) {
            return "";
        } else {
            return cleanUp(val.trim());
        }
    }

    private static String cleanUp(String field) {
        field = field.replace("BRTLoan", "BRTALoan")
                .replace("'", "")
                .replace("_", "-")
                .replace("\\", " ")
                .replace("/", " ")
                .replace("[", " ")
                .replace("]", " ")
                .replace(",", "")
                .replace("\"", "")
                .replace("`", "")
                .replaceAll("[^\\x00-\\x7F]", "")
                .replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "")
                .replaceAll("\\p{C}", "");
        return field.trim();
    }
}
