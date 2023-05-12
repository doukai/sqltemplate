package io.sqltemplate.core.jdbc;

import com.google.common.base.CaseFormat;
import jakarta.transaction.Transactional;
import org.stringtemplate.v4.ST;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.sqltemplate.core.utils.TemplateInstanceUtil.TEMPLATE_INSTANCE_UTIL;

public class JDBCAdapter<T> {

    private Transactional.TxType txType = Transactional.TxType.REQUIRED;
    private Class<?>[] rollbackOn = {};
    private Class<?>[] dontRollbackOn = {};

    private final String templateName;

    private final String instanceName;

    private final Map<String, Object> params;

    public JDBCAdapter(String templateName, String instanceName, Map<String, Object> params, Transactional.TxType txType, Class<?>[] rollbackOn, Class<?>[] dontRollbackOn) {
        this(templateName, instanceName, params);
        this.txType = txType;
        this.rollbackOn = rollbackOn;
        this.dontRollbackOn = dontRollbackOn;
    }

    public JDBCAdapter(String templateName, String instanceName, Map<String, Object> params, Transactional.TxType txType) {
        this(templateName, instanceName, params);
        this.txType = txType;
    }

    public JDBCAdapter(String templateName, String instanceName, Map<String, Object> params) {
        this.templateName = templateName;
        this.instanceName = instanceName;
        this.params = params;
    }

    public T query() throws SQLException {
        try {
            ST instance = TEMPLATE_INSTANCE_UTIL.getInstance(templateName, instanceName, params);
            Connection connection = JDBCTransactionManager.getConnection();
            JDBCTransactionManager.begin();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(instance.render());
            JDBCTransactionManager.commit();
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
        } catch (SQLException e) {
            JDBCTransactionManager.rollback(e);
            throw new RuntimeException(e);
        }
    }

    public List<T> queryList() {
        try {
            ST instance = TEMPLATE_INSTANCE_UTIL.getInstance(templateName, instanceName, params);
            Connection connection = JDBCTransactionManager.getConnection();
            JDBCTransactionManager.begin();
            Statement statement = null;
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(instance.render());
            JDBCTransactionManager.commit();
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
        } catch (SQLException e) {
            JDBCTransactionManager.rollback(e);
            throw new RuntimeException(e);
        }
    }

    public Long update() throws SQLException {
        try {
            ST instance = TEMPLATE_INSTANCE_UTIL.getInstance(templateName, instanceName, params);
            Connection connection = JDBCTransactionManager.getConnection();
            JDBCTransactionManager.begin();
            Statement statement = connection.createStatement();
            long updated = statement.executeUpdate(instance.render());
            JDBCTransactionManager.commit();
            return updated;
        } catch (SQLException e) {
            JDBCTransactionManager.rollback(e);
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    protected T map(Map<String, Object> result) {
        return (T) result;
    }

    private List<T> mapList(List<Map<String, Object>> list) {
        return list.stream().map(this::map).collect(Collectors.toList());
    }
}
