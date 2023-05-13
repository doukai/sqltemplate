package io.sqltemplate.core.jdbc;

import com.google.common.base.CaseFormat;
import io.sqltemplate.core.adapter.Adapter;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.TransactionRequiredException;
import jakarta.transaction.Transactional;
import org.stringtemplate.v4.ST;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
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
        try {
            ST instance = TEMPLATE_INSTANCE_UTIL.getInstance(getTemplateName(), getInstanceName(), getParams());
            JDBCTransactionManager.begin(getTxType());
            Connection connection = JDBCTransactionManager.getConnection();
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
        } catch (SQLException | TransactionRequiredException | InvalidTransactionException | NotSupportedException e) {
            JDBCTransactionManager.rollback(e, getRollbackOn(), getDontRollbackOn());
            throw new RuntimeException(e);
        }
    }

    public List<T> queryList() {
        try {
            ST instance = TEMPLATE_INSTANCE_UTIL.getInstance(getTemplateName(), getInstanceName(), getParams());
            JDBCTransactionManager.begin(getTxType());
            Connection connection = JDBCTransactionManager.getConnection();
            Statement statement = connection.createStatement();
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
        } catch (SQLException | TransactionRequiredException | InvalidTransactionException | NotSupportedException e) {
            JDBCTransactionManager.rollback(e, getRollbackOn(), getDontRollbackOn());
            throw new RuntimeException(e);
        }
    }

    public Long update() {
        try {
            ST instance = TEMPLATE_INSTANCE_UTIL.getInstance(getTemplateName(), getInstanceName(), getParams());
            JDBCTransactionManager.begin(getTxType());
            Connection connection = JDBCTransactionManager.getConnection();
            Statement statement = connection.createStatement();
            long updated = statement.executeUpdate(instance.render());
            JDBCTransactionManager.commit();
            return updated;
        } catch (SQLException | TransactionRequiredException | InvalidTransactionException | NotSupportedException e) {
            JDBCTransactionManager.rollback(e, getRollbackOn(), getDontRollbackOn());
            throw new RuntimeException(e);
        }
    }
}
