package io.sqltemplate.showcase.connection;

import com.google.auto.service.AutoService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

@AutoService(io.sqltemplate.spi.connection.JDBCConnectionProvider.class)
public class JDBCConnectionProvider implements io.sqltemplate.spi.connection.JDBCConnectionProvider {

    public static DataSource createDataSource() {
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
            return createDataSource().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
