package io.sqltemplate.gradle.task;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.sqltemplate.gradle.config.JDBCConfig;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class GenerateRecord extends DefaultTask {

    protected static final String MAIN_PATH = "src".concat(File.separator).concat("main");
    protected static final String MAIN_JAVA_PATH = MAIN_PATH.concat(File.separator).concat("java");
    protected static final String MAIN_RESOURCES_PATH = MAIN_PATH.concat(File.separator).concat("resources");

    @TaskAction
    public void generateRecord() {
        try {
            DatabaseMetaData databaseMetaData = createConnection().getMetaData();
            
        } catch (SQLException e) {
            throw new TaskExecutionException(this, e);
        }
    }

    public Connection createConnection() {
        JDBCConfig jdbcConfig = getProject().getExtensions().findByType(JDBCConfig.class);
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcConfig.getJdbcUrl());
        config.setUsername(jdbcConfig.getUsername());
        config.setPassword(jdbcConfig.getPassword());
        config.addDataSourceProperty("cachePrepStmts", jdbcConfig.getCachePrepStmts());
        config.addDataSourceProperty("prepStmtCacheSize", jdbcConfig.getPrepStmtCacheSize());
        config.addDataSourceProperty("prepStmtCacheSqlLimit", jdbcConfig.getPrepStmtCacheSqlLimit());
        config.addDataSourceProperty("useServerPrepStmts", jdbcConfig.getUseServerPrepStmts());
        config.addDataSourceProperty("useLocalSessionState", jdbcConfig.getUseLocalSessionState());
        config.addDataSourceProperty("rewriteBatchedStatements", jdbcConfig.getRewriteBatchedStatements());
        config.addDataSourceProperty("cacheResultSetMetadata", jdbcConfig.getCacheResultSetMetadata());
        config.addDataSourceProperty("cacheServerConfiguration", jdbcConfig.getCacheServerConfiguration());
        config.addDataSourceProperty("elideSetAutoCommits", jdbcConfig.getElideSetAutoCommits());
        config.addDataSourceProperty("maintainTimeStats", jdbcConfig.getMaintainTimeStats());
        HikariDataSource ds = new HikariDataSource(config);
        try {
            return ds.getConnection();
        } catch (SQLException e) {
            throw new TaskExecutionException(this, e);
        }
    }
}
