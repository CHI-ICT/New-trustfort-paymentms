package com.chh.trustfort.accounting.tenant;

/**
 *
 * @author DOfoleta
 */

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class HibernateMultiTenancyConfig {

    private final DataSource defaultDataSource;

    @Autowired
    public HibernateMultiTenancyConfig(DataSource defaultDataSource) {
        this.defaultDataSource = defaultDataSource;
    }

    @Bean
    public MultiTenantConnectionProvider multiTenantConnectionProvider() {
        return new MultiTenantConnectionProviderImpl(defaultDataSource);
    }

    @Bean
    public CurrentTenantIdentifierResolver tenantIdentifierResolver2() {
        return new TenantIdentifierResolver();
    }
    
}
