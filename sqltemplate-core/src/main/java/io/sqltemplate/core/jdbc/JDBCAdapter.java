package io.sqltemplate.core.jdbc;

import com.google.common.base.CaseFormat;
import io.sqltemplate.core.adapter.Adapter;
import io.sqltemplate.spi.transaction.JDBCTransactionManager;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.TransactionRequiredException;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static io.sqltemplate.core.utils.TemplateInstanceUtil.TEMPLATE_INSTANCE_UTIL;

public class JDBCAdapter<T> extends Adapter<T> {

    public JDBCAdapter() {
    }

    public JDBCAdapter(String templateName, String instanceName, Map<String, Object> params) {
        super(templateName, instanceName, params);
    }

    public JDBCAdapter(String templateName, String instanceName, Map<String, Object> params, Transactional.TxType txType, Class<?>[] rollbackOn, Class<?>[] dontRollbackOn) {
        super(templateName, instanceName, params, txType, rollbackOn, dontRollbackOn);
    }

    public JDBCAdapter(String templateName, String instanceName, Map<String, Object> params, Transactional transactional) {
        super(templateName, instanceName, params, transactional);
    }

    public T query() {
        Map.Entry<String, List<Object>> sqlWithParams = TEMPLATE_INSTANCE_UTIL.getSQLWithParams(getTemplateName(), getInstanceName(), getParams());
        String sql = sqlWithParams.getKey();
        List<Object> params = sqlWithParams.getValue();
        String tid = null;
        try {
            tid = JDBCTransactionManager.begin(getTxType());
            Connection connection = JDBCTransactionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = setParams(preparedStatement, params).executeQuery();
            JDBCTransactionManager.commit(tid);
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
        } catch (SQLException | TransactionRequiredException | InvalidTransactionException | NotSupportedException e) {
            JDBCTransactionManager.rollback(tid, e, getRollbackOn(), getDontRollbackOn());
            throw new RuntimeException(e);
        }
    }

    public List<T> queryList() {
        Map.Entry<String, List<Object>> sqlWithParams = TEMPLATE_INSTANCE_UTIL.getSQLWithParams(getTemplateName(), getInstanceName(), getParams());
        String sql = sqlWithParams.getKey();
        List<Object> params = sqlWithParams.getValue();
        String tid = null;
        try {
            tid = JDBCTransactionManager.begin(getTxType());
            Connection connection = JDBCTransactionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = setParams(preparedStatement, params).executeQuery();
            JDBCTransactionManager.commit(tid);
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
        } catch (SQLException | TransactionRequiredException | InvalidTransactionException | NotSupportedException e) {
            JDBCTransactionManager.rollback(tid, e, getRollbackOn(), getDontRollbackOn());
            throw new RuntimeException(e);
        }
    }

    public Long update() {
        Map.Entry<String, List<Object>> sqlWithParams = TEMPLATE_INSTANCE_UTIL.getSQLWithParams(getTemplateName(), getInstanceName(), getParams());
        String sql = sqlWithParams.getKey();
        List<Object> params = sqlWithParams.getValue();
        String tid = null;
        try {
            tid = JDBCTransactionManager.begin(getTxType());
            Connection connection = JDBCTransactionManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            long updated = setParams(preparedStatement, params).executeUpdate();
            JDBCTransactionManager.commit(tid);
            return updated;
        } catch (SQLException | TransactionRequiredException | InvalidTransactionException | NotSupportedException e) {
            JDBCTransactionManager.rollback(tid, e, getRollbackOn(), getDontRollbackOn());
            throw new RuntimeException(e);
        }
    }

    protected PreparedStatement setParams(PreparedStatement preparedStatement, List<Object> params) throws SQLException {
        for (int i = 1; i <= params.size(); i++) {
            Object param = params.get(i - 1);
            if (param == null) {
                preparedStatement.setNull(i, 0);
            } else if (param instanceof Integer) {
                preparedStatement.setInt(i, (Integer) param);
            } else if (param instanceof Long) {
                preparedStatement.setLong(i, (Long) param);
            } else if (param instanceof Short) {
                preparedStatement.setShort(i, (Short) param);
            } else if (param instanceof Double) {
                preparedStatement.setDouble(i, (Double) param);
            } else if (param instanceof Float) {
                preparedStatement.setFloat(i, (Float) param);
            } else if (param instanceof BigDecimal) {
                preparedStatement.setBigDecimal(i, (BigDecimal) param);
            } else if (param instanceof String) {
                preparedStatement.setString(i, (String) param);
            } else if (param instanceof LocalDate) {
                preparedStatement.setDate(i, Date.valueOf((LocalDate) param));
            } else if (param instanceof LocalTime) {
                preparedStatement.setTime(i, Time.valueOf((LocalTime) param));
            } else if (param instanceof LocalDateTime) {
                preparedStatement.setTimestamp(i, Timestamp.valueOf((LocalDateTime) param));
            } else if (param instanceof Byte) {
                preparedStatement.setByte(i, (Byte) param);
            } else {
                preparedStatement.setObject(i, param);
            }
        }
        return preparedStatement;
    }
}
