package io.sqltemplate.showcase.test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import io.sqltemplate.showcase.test.connection.HikariCPJDBCConnectionProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.sql;
import static com.ninja_squad.dbsetup.operation.CompositeOperation.sequenceOf;

public class Setup {

    private static final DataSourceDestination dataSourceDestination = new DataSourceDestination(HikariCPJDBCConnectionProvider.dataSource);

    public static final List<Map<String, Object>> users = new ArrayList<Map<String, Object>>() {{
        add(new HashMap<String, Object>() {{
            put("id", 1);
            put("name", "Robin Castillo");
            put("login", "castillo");
            put("password", "96954");
            put("age", 9);
            put("organization_id", 3);
        }});
        add(new HashMap<String, Object>() {{
            put("id", 2);
            put("name", "Kelly Villarreal");
            put("login", "villarreal");
            put("password", "54368");
            put("age", 18);
            put("organization_id", 3);
        }});
        add(new HashMap<String, Object>() {{
            put("id", 3);
            put("name", "Malia England");
            put("login", "england");
            put("password", "68925");
            put("age", 27);
            put("organization_id", 2);
        }});
        add(new HashMap<String, Object>() {{
            put("id", 4);
            put("name", "Neha Chambers");
            put("login", "chambers");
            put("password", "47502");
            put("age", 36);
            put("organization_id", 1);
        }});
    }};

    public static final List<Map<String, Object>> organizations = new ArrayList<Map<String, Object>>() {{
        add(new HashMap<String, Object>() {{
            put("id", 1);
            put("name", "Apple");
        }});
        add(new HashMap<String, Object>() {{
            put("id", 2);
            put("name", "Microsoft");
        }});
        add(new HashMap<String, Object>() {{
            put("id", 3);
            put("name", "Google");
        }});
    }};

    public static final List<Map<String, Object>> roles = new ArrayList<Map<String, Object>>() {{
        add(new HashMap<String, Object>() {{
            put("id", 1);
            put("name", "User");
        }});
        add(new HashMap<String, Object>() {{
            put("id", 2);
            put("name", "Admin");
        }});
    }};

    public static final List<Map<String, Object>> userRoles = new ArrayList<Map<String, Object>>() {{
        add(new HashMap<String, Object>() {{
            put("user_id", 1);
            put("role_id", 1);
        }});
        add(new HashMap<String, Object>() {{
            put("user_id", 1);
            put("role_id", 2);
        }});
    }};

    public static final Operation CREATE_TABLES =
            sequenceOf(
                    sql("CREATE TABLE IF NOT EXISTS `user`  (\n" +
                            "  `id` int(255) NOT NULL,\n" +
                            "  `login` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,\n" +
                            "  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,\n" +
                            "  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,\n" +
                            "  `age` int(11) NULL DEFAULT NULL,\n" +
                            "  `disable` tinyint(1) NULL DEFAULT NULL,\n" +
                            "  `sex` enum('MAN','FEMALE') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,\n" +
                            "  `organization_id` int(11) NULL DEFAULT NULL,\n" +
                            "  `is_deprecated` tinyint(1) NULL DEFAULT NULL,\n" +
                            "  `version` int(11) NULL DEFAULT NULL,\n" +
                            "  PRIMARY KEY (`id`) USING BTREE\n" +
                            ") ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;"),
                    sql("CREATE TABLE IF NOT EXISTS `organization`  (\n" +
                            "  `id` int(255) NOT NULL,\n" +
                            "  `above_id` int(11) NULL DEFAULT NULL,\n" +
                            "  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,\n" +
                            "  `is_deprecated` tinyint(1) NULL DEFAULT NULL,\n" +
                            "  `version` int(11) NULL DEFAULT NULL,\n" +
                            "  PRIMARY KEY (`id`) USING BTREE\n" +
                            ") ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;"),
                    sql("CREATE TABLE IF NOT EXISTS `role`  (\n" +
                            "  `id` int(255) NOT NULL,\n" +
                            "  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,\n" +
                            "  `is_deprecated` tinyint(1) NULL DEFAULT NULL,\n" +
                            "  `version` int(11) NULL DEFAULT NULL,\n" +
                            "  PRIMARY KEY (`id`) USING BTREE\n" +
                            ") ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;"),
                    sql("CREATE TABLE IF NOT EXISTS `user_role`  (\n" +
                            "  `id` int(255) NOT NULL AUTO_INCREMENT,\n" +
                            "  `user_id` int(11) NULL DEFAULT NULL,\n" +
                            "  `role_id` int(11) NULL DEFAULT NULL,\n" +
                            "  `is_deprecated` tinyint(1) NULL DEFAULT NULL,\n" +
                            "  `version` int(11) NULL DEFAULT NULL,\n" +
                            "  PRIMARY KEY (`id`) USING BTREE\n" +
                            ") ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;")
            );

    public static final Operation DELETE_ALL =
            deleteAllFrom("user", "organization", "role", "user_role");

    public static final Operation INSERT_USERS =
            sequenceOf(
                    users.stream()
                            .map(user ->
                                    insertInto("user")
                                            .columns(user.keySet().toArray(new String[]{}))
                                            .values(user)
                                            .build())
                            .collect(Collectors.toList())
            );

    public static final Operation INSERT_ORGANIZATIONS =
            sequenceOf(
                    organizations.stream()
                            .map(organization ->
                                    insertInto("organization")
                                            .columns(organization.keySet().toArray(new String[]{}))
                                            .values(organization)
                                            .build())
                            .collect(Collectors.toList())
            );

    public static final Operation INSERT_ROLES =
            sequenceOf(
                    roles.stream()
                            .map(role ->
                                    insertInto("role")
                                            .columns(role.keySet().toArray(new String[]{}))
                                            .values(role)
                                            .build())
                            .collect(Collectors.toList())
            );

    public static final Operation INSERT_USER_ROLES =
            sequenceOf(
                    userRoles.stream()
                            .map(userRole ->
                                    insertInto("user_role")
                                            .columns(userRole.keySet().toArray(new String[]{}))
                                            .values(userRole)
                                            .build())
                            .collect(Collectors.toList())
            );

    public static void tableInit() {
        Operation operation =
                sequenceOf(
                        CREATE_TABLES
                );
        DbSetup dbSetup = new DbSetup(dataSourceDestination, operation);
        dbSetup.launch();
    }

    public static void tableClear() {
        Operation operation =
                sequenceOf(
                        DELETE_ALL
                );
        DbSetup dbSetup = new DbSetup(dataSourceDestination, operation);
        dbSetup.launch();
    }

    public static void tableInsert() {
        Operation operation =
                sequenceOf(
                        INSERT_USERS,
                        INSERT_ORGANIZATIONS,
                        INSERT_ROLES,
                        INSERT_USER_ROLES
                );
        DbSetup dbSetup = new DbSetup(dataSourceDestination, operation);
        dbSetup.launch();
    }

    public static void userTableInsert() {
        Operation operation =
                sequenceOf(
                        INSERT_USERS
                );
        DbSetup dbSetup = new DbSetup(dataSourceDestination, operation);
        dbSetup.launch();
    }

    public static void organizationTableInsert() {
        Operation operation =
                sequenceOf(
                        INSERT_ORGANIZATIONS
                );
        DbSetup dbSetup = new DbSetup(dataSourceDestination, operation);
        dbSetup.launch();
    }
}
