package io.sqltemplate.gradle.config;

public class JDBCConfig {

    private String jdbcUrl;
    private String username;
    private String password;
    private Boolean cachePrepStmts = true;
    private Integer prepStmtCacheSize = 250;
    private Integer prepStmtCacheSqlLimit = 2048;
    private Boolean useServerPrepStmts = true;
    private Boolean useLocalSessionState = true;
    private Boolean rewriteBatchedStatements = true;
    private Boolean cacheResultSetMetadata = true;
    private Boolean cacheServerConfiguration = true;
    private Boolean elideSetAutoCommits = true;
    private Boolean maintainTimeStats = false;

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getCachePrepStmts() {
        return cachePrepStmts;
    }

    public void setCachePrepStmts(Boolean cachePrepStmts) {
        this.cachePrepStmts = cachePrepStmts;
    }

    public Integer getPrepStmtCacheSize() {
        return prepStmtCacheSize;
    }

    public void setPrepStmtCacheSize(Integer prepStmtCacheSize) {
        this.prepStmtCacheSize = prepStmtCacheSize;
    }

    public Integer getPrepStmtCacheSqlLimit() {
        return prepStmtCacheSqlLimit;
    }

    public void setPrepStmtCacheSqlLimit(Integer prepStmtCacheSqlLimit) {
        this.prepStmtCacheSqlLimit = prepStmtCacheSqlLimit;
    }

    public Boolean getUseServerPrepStmts() {
        return useServerPrepStmts;
    }

    public void setUseServerPrepStmts(Boolean useServerPrepStmts) {
        this.useServerPrepStmts = useServerPrepStmts;
    }

    public Boolean getUseLocalSessionState() {
        return useLocalSessionState;
    }

    public void setUseLocalSessionState(Boolean useLocalSessionState) {
        this.useLocalSessionState = useLocalSessionState;
    }

    public Boolean getRewriteBatchedStatements() {
        return rewriteBatchedStatements;
    }

    public void setRewriteBatchedStatements(Boolean rewriteBatchedStatements) {
        this.rewriteBatchedStatements = rewriteBatchedStatements;
    }

    public Boolean getCacheResultSetMetadata() {
        return cacheResultSetMetadata;
    }

    public void setCacheResultSetMetadata(Boolean cacheResultSetMetadata) {
        this.cacheResultSetMetadata = cacheResultSetMetadata;
    }

    public Boolean getCacheServerConfiguration() {
        return cacheServerConfiguration;
    }

    public void setCacheServerConfiguration(Boolean cacheServerConfiguration) {
        this.cacheServerConfiguration = cacheServerConfiguration;
    }

    public Boolean getElideSetAutoCommits() {
        return elideSetAutoCommits;
    }

    public void setElideSetAutoCommits(Boolean elideSetAutoCommits) {
        this.elideSetAutoCommits = elideSetAutoCommits;
    }

    public Boolean getMaintainTimeStats() {
        return maintainTimeStats;
    }

    public void setMaintainTimeStats(Boolean maintainTimeStats) {
        this.maintainTimeStats = maintainTimeStats;
    }
}
