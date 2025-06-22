package com.chh.trustfort.payment.tenant;

/**
 *
 * @author DOfoleta
 */
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        return TenantContext.getTenantId(); // Fetch the tenant ID safely
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}
