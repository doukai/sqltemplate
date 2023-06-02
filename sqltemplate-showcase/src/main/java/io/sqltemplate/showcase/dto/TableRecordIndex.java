package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.RecordIndex;
import io.sqltemplate.active.record.TableRecord;
import java.lang.String;
import java.util.function.Supplier;

public class TableRecordIndex implements RecordIndex {
  static {
    TableRecord.registerEntityClass(Organization.class);
    TableRecord.registerEntityClass(User.class);
  }

  public Supplier<TableRecord<?>> getRecordSupplier(String tableName) {
    if (tableName.equals("organization")) {
      return Organization::new;
    } else if (tableName.equals("user")) {
      return User::new;
    }
    return null;
  }
}
