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

import static io.sqltemplate.core.template.TemplateInstance.TEMPLATE_INSTANCE;

public abstract class JDBCAdapter<T> extends Adapter<T> {

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
        String sql = TEMPLATE_INSTANCE.render(getTemplateName(), getInstanceName(), getParams());
        String tid = null;
        try {
            tid = JDBCTransactionManager.begin(getTxType());
            Connection connection = JDBCTransactionManager.getConnection();
            NamedParameterPreparedStatement namedParameterPreparedStatement = NamedParameterPreparedStatement.createNamedParameterPreparedStatement(connection, sql);
            ResultSet resultSet = setParams(namedParameterPreparedStatement, getParams()).executeQuery();
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int columnCount = resultSetMetaData.getColumnCount();

            T result = null;
            if (resultSet.next()) {
                Map<String, Object> row = new HashMap<>(columnCount);
                for (int i = 1; i <= columnCount; ++i) {
                    row.put(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, resultSetMetaData.getColumnName(i)), resultSet.getObject(i));
                }
                result = map(row);
            }
            JDBCTransactionManager.commit(tid);
            return result;
        } catch (SQLException | TransactionRequiredException | InvalidTransactionException | NotSupportedException e) {
            JDBCTransactionManager.rollback(tid, e, getRollbackOn(), getDontRollbackOn());
            throw new RuntimeException(e);
        }
    }

    public List<T> queryList() {
        String sql = TEMPLATE_INSTANCE.render(getTemplateName(), getInstanceName(), getParams());
        String tid = null;
        try {
            tid = JDBCTransactionManager.begin(getTxType());
            Connection connection = JDBCTransactionManager.getConnection();
            NamedParameterPreparedStatement namedParameterPreparedStatement = NamedParameterPreparedStatement.createNamedParameterPreparedStatement(connection, sql);
            ResultSet resultSet = setParams(namedParameterPreparedStatement, getParams()).executeQuery();
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
            List<T> result = mapList(list);
            JDBCTransactionManager.commit(tid);
            return result;
        } catch (SQLException | TransactionRequiredException | InvalidTransactionException | NotSupportedException e) {
            JDBCTransactionManager.rollback(tid, e, getRollbackOn(), getDontRollbackOn());
            throw new RuntimeException(e);
        }
    }

    public Long update() {
        String sql = TEMPLATE_INSTANCE.render(getTemplateName(), getInstanceName(), getParams());
        String tid = null;
        try {
            tid = JDBCTransactionManager.begin(getTxType());
            Connection connection = JDBCTransactionManager.getConnection();
            NamedParameterPreparedStatement namedParameterPreparedStatement = NamedParameterPreparedStatement.createNamedParameterPreparedStatement(connection, sql);
            long updated = setParams(namedParameterPreparedStatement, getParams()).executeUpdate();
            JDBCTransactionManager.commit(tid);
            return updated;
        } catch (SQLException | TransactionRequiredException | InvalidTransactionException | NotSupportedException e) {
            JDBCTransactionManager.rollback(tid, e, getRollbackOn(), getDontRollbackOn());
            throw new RuntimeException(e);
        }
    }

    protected PreparedStatement setParams(NamedParameterPreparedStatement preparedStatement, Map<String, Object> params) throws SQLException {
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (entry.getValue() == null) {
                preparedStatement.setNull(entry.getKey(), 0);
            } else if (entry.getValue() instanceof Integer) {
                preparedStatement.setInt(entry.getKey(), (Integer) entry.getValue());
            } else if (entry.getValue() instanceof Long) {
                preparedStatement.setLong(entry.getKey(), (Long) entry.getValue());
            } else if (entry.getValue() instanceof Short) {
                preparedStatement.setShort(entry.getKey(), (Short) entry.getValue());
            } else if (entry.getValue() instanceof Double) {
                preparedStatement.setDouble(entry.getKey(), (Double) entry.getValue());
            } else if (entry.getValue() instanceof Float) {
                preparedStatement.setFloat(entry.getKey(), (Float) entry.getValue());
            } else if (entry.getValue() instanceof BigDecimal) {
                preparedStatement.setBigDecimal(entry.getKey(), (BigDecimal) entry.getValue());
            } else if (entry.getValue() instanceof String) {
                preparedStatement.setString(entry.getKey(), (String) entry.getValue());
            } else if (entry.getValue() instanceof LocalDate) {
                preparedStatement.setDate(entry.getKey(), Date.valueOf((LocalDate) entry.getValue()));
            } else if (entry.getValue() instanceof LocalTime) {
                preparedStatement.setTime(entry.getKey(), Time.valueOf((LocalTime) entry.getValue()));
            } else if (entry.getValue() instanceof LocalDateTime) {
                preparedStatement.setTimestamp(entry.getKey(), Timestamp.valueOf((LocalDateTime) entry.getValue()));
            } else if (entry.getValue() instanceof Byte) {
                preparedStatement.setByte(entry.getKey(), (Byte) entry.getValue());
            } else {
                preparedStatement.setObject(entry.getKey(), entry.getValue());
            }
        }
        return preparedStatement;
    }
}
