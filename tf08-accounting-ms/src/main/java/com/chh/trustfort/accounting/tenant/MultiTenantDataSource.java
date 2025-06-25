package com.chh.trustfort.accounting.tenant;

/**
 *
 * @author DOfoleta
 */

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;

public class MultiTenantDataSource extends AbstractRoutingDataSource {

    private final Map<Object, Object> dataSources;

    public MultiTenantDataSource(Map<Object, Object> dataSources) {
        this.dataSources = dataSources;
        this.setTargetDataSources(dataSources);
        this.afterPropertiesSet();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContext.getTenantId();
    }

    public void addTenant(String tenantId, DataSource dataSource) {
        this.dataSources.put(tenantId, dataSource);
        this.afterPropertiesSet();
    }

    public static DataSource createDataSource(String url, String username, String password) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName("org.postgresql.Driver");
        return dataSource;
    }
}
