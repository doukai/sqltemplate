package io.sqltemplate.showcase.dto;

import io.sqltemplate.active.record.RecordIndex;
import io.sqltemplate.active.record.TableRecord;
import java.lang.String;
import java.util.function.Supplier;

public class TableRecordIndex implements RecordIndex {
  public Supplier<TableRecord<?>> getRecordSupplier(String tableName) {
    if (tableName.equals("__directive")) {
      return Directive::new;
    } else if (tableName.equals("__directive_locations")) {
      return DirectiveLocations::new;
    } else if (tableName.equals("__enum_value")) {
      return EnumValue::new;
    } else if (tableName.equals("__field")) {
      return Field::new;
    } else if (tableName.equals("__input_value")) {
      return InputValue::new;
    } else if (tableName.equals("__schema")) {
      return Schema::new;
    } else if (tableName.equals("__type")) {
      return Type::new;
    } else if (tableName.equals("__type_interfaces")) {
      return TypeInterfaces::new;
    } else if (tableName.equals("__type_possible_types")) {
      return TypePossibleTypes::new;
    } else if (tableName.equals("organization")) {
      return Organization::new;
    } else if (tableName.equals("role")) {
      return Role::new;
    } else if (tableName.equals("role_role_type")) {
      return RoleRoleType::new;
    } else if (tableName.equals("user")) {
      return User::new;
    } else if (tableName.equals("user_mobile_numbers")) {
      return UserMobileNumbers::new;
    } else if (tableName.equals("user_profile")) {
      return UserProfile::new;
    } else if (tableName.equals("user_role")) {
      return UserRole::new;
    }
    return null;
  }
}
