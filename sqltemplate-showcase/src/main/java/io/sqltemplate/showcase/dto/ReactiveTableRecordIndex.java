package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.ReactiveRecord;
import io.sqltemplate.active.record.ReactiveRecordIndex;
import io.sqltemplate.active.record.TableRecord;
import java.lang.String;
import java.util.function.Supplier;

public class ReactiveTableRecordIndex implements ReactiveRecordIndex {
  static {
    TableRecord.registerEntityClass(ReactiveOrganization.class);
    TableRecord.registerEntityClass(ReactiveRole.class);
    TableRecord.registerEntityClass(ReactiveUser.class);
    TableRecord.registerEntityClass(ReactiveUserRole.class);
  }

  public Supplier<ReactiveRecord<?>> getRecordSupplier(String tableName) {
    if (tableName.equals("organization")) {
      return ReactiveOrganization::new;
    } else if (tableName.equals("role")) {
      return ReactiveRole::new;
    } else if (tableName.equals("user")) {
      return ReactiveUser::new;
    } else if (tableName.equals("user_role")) {
      return ReactiveUserRole::new;
    }
    return null;
  }
}
