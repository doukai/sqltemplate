package io.sqltemplate.showcase.test.connection;

import com.google.auto.service.AutoService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.sqltemplate.spi.connection.JDBCConnectionProvider;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

@AutoService(JDBCConnectionProvider.class)
public class HikariCPJDBCConnectionProvider implements JDBCConnectionProvider {

    public static DataSource dataSource = createDataSource();

    private static DataSource createDataSource() {
        InputStream resourceStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("jdbc.properties");
        Properties jdbcProperties = new Properties();
        try {
            jdbcProperties.load(resourceStream);
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcProperties.getProperty("url"));
            config.setUsername(jdbcProperties.getProperty("user"));
            config.setPassword(jdbcProperties.getProperty("password"));
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            return new HikariDataSource(config);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Connection createConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
