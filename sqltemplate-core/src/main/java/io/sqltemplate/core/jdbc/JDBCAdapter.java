package io.sqltemplate.core.jdbc;

import com.google.common.base.CaseFormat;
import io.sqltemplate.spi.annotation.TemplateType;
import org.stringtemplate.v4.ST;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.sqltemplate.core.utils.TemplateInstanceUtil.TEMPLATE_INSTANCE_UTIL;

public abstract class JDBCAdapter<T> {

    private String templateName;

    private TemplateType type;

    private String instanceName;

    private Map<String, Object> params;

    private final ConnectionProvider connectionProvider;

    public JDBCAdapter(String templateName, TemplateType type, String instanceName, Map<String, Object> params) {
        this.templateName = templateName;
        this.type = type;
        this.instanceName = instanceName;
        this.params = params;
        this.connectionProvider = Objects.requireNonNull(ConnectionProvider.provider());
    }

    public JDBCAdapter<T> setTemplateName(String templateName) {
        this.templateName = templateName;
        return this;
    }

    public JDBCAdapter<T> setType(TemplateType type) {
        this.type = type;
        return this;
    }

    public JDBCAdapter<T> setInstanceName(String instanceName) {
        this.instanceName = instanceName;
        return this;
    }

    public JDBCAdapter<T> setParams(Map<String, Object> params) {
        this.params = params;
        return this;
    }

    public T query() throws SQLException {
        ST instance = TEMPLATE_INSTANCE_UTIL.getInstance(templateName, type, instanceName, params);
        Statement statement = connectionProvider.createConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(instance.render());
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();

        if (resultSet.next()) {
            Map<String, Object> row = new HashMap<>(columnCount);
            for (int i = 1; i <= columnCount; ++i) {
                row.put(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, resultSetMetaData.getColumnName(i)), resultSet.getObject(i));
            }
            return map(row);
        }
        return null;
    }

    public List<T> queryList() throws SQLException {
        ST instance = TEMPLATE_INSTANCE_UTIL.getInstance(templateName, type, instanceName, params);
        Statement statement = connectionProvider.createConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(instance.render());
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();

        List<Map<String, Object>> list = new ArrayList<>(50);
        while (resultSet.next()) {
            Map<String, Object> row = new HashMap<>(columnCount);
            for (int i = 1; i <= columnCount; ++i) {
                row.put(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, resultSetMetaData.getColumnName(i)), resultSet.getObject(i));
            }
            list.add(row);
        }
        return mapList(list);
    }

    public Integer update() throws SQLException {
        ST instance = TEMPLATE_INSTANCE_UTIL.getInstance(templateName, type, instanceName, params);
        Statement statement = connectionProvider.createConnection().createStatement();
        return statement.executeUpdate(instance.render());
    }

    protected abstract T map(Map<String, Object> result);

    private List<T> mapList(List<Map<String, Object>> list) {
        return list.stream().map(this::map).collect(Collectors.toList());
    }
}
