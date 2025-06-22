package com.chh.trustfort.accounting.tenant;

/**
 *
 * @author DOfoleta
 */

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider {
    
    @Autowired
    Environment env;
    
    private final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();
    private final DataSource defaultDataSource;

    public MultiTenantConnectionProviderImpl(DataSource defaultDataSource) {
        this.defaultDataSource = defaultDataSource;
    }

    public void addTenantDataSource(String tenantId, DataSource dataSource) {
        dataSources.put(tenantId, dataSource);
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        if (tenantIdentifier == null || !dataSources.containsKey(tenantIdentifier)) {
            tenantIdentifier = env.getProperty("default-tenant"); // Fallback to the default tenant
//            tenantIdentifier = env.getProperty("chi"); // Fallback to the default tenant
        }
        DataSource dataSource = dataSources.getOrDefault(tenantIdentifier, defaultDataSource);
        return dataSource.getConnection();
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return defaultDataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public void releaseConnection(String string, Connection cnctn) throws SQLException {
        cnctn.close();
    }

    @Override
    public boolean isUnwrappableAs(Class type) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
