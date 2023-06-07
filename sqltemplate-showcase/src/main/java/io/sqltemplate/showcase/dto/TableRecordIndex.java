package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.Record;
import io.sqltemplate.active.record.RecordIndex;
import io.sqltemplate.active.record.TableRecord;
import java.lang.String;
import java.util.function.Supplier;

public class TableRecordIndex implements RecordIndex {
  static {
    TableRecord.registerEntityClass(Organization.class);
    TableRecord.registerEntityClass(Role.class);
    TableRecord.registerEntityClass(User.class);
    TableRecord.registerEntityClass(UserRole.class);
  }

  public Supplier<Record<?>> getRecordSupplier(String tableName) {
    if (tableName.equals("organization")) {
      return Organization::new;
    } else if (tableName.equals("role")) {
      return Role::new;
    } else if (tableName.equals("user")) {
      return User::new;
    } else if (tableName.equals("user_role")) {
      return UserRole::new;
    }
    return null;
  }
}
