package com.chh.trustfort.payment.tenant;

/**
 *
 * @author DOfoleta
 */
public class TenantContext {
       
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
    private static final String DEFAULT_TENANT = "trustfort"; // Set your default tenant ID
    
//    private static final String DEFAULT_TENANT = "chi"; // Set your default tenant ID

    public static void setTenantId(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }

    public static String getTenantId() {
        String tenant = CURRENT_TENANT.get();
        System.out.println("Schema initialized for tenant: " + tenant);
        return (tenant != null) ? tenant : DEFAULT_TENANT; // Ensure it never returns null
    }

    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
