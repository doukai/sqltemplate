package io.sqltemplate.showcase.templates;

import io.sqltemplate.core.jdbc.JDBCAdapter;
import io.sqltemplate.core.r2dbc.R2DBCAdapter;
import io.sqltemplate.spi.annotation.TemplateType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserTemplateTest implements UserTemplate {

    @Override
    public User getUser(String name) throws SQLException {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("name", name);
        }};
        return new JDBCAdapter<User>("", TemplateType.DIR, "", params) {
            @Override
            protected User map(Map<String, Object> result) {
                User user = new User();
                if (result.get("age") != null) {
                    user.setAge((Integer) result.get("age"));
                }
                if (result.get("sex") != null) {
                    user.setSex(Sex.valueOf((String) result.get("sex")));
                }
                return user;
            }
        }.query();
    }

    @Override
    public List<User> getUserList(Sex sex, int age) throws SQLException {
        return new JDBCAdapter<User>("", TemplateType.DIR, "", null) {
            @Override
            protected User map(Map<String, Object> result) {
                return null;
            }
        }.queryList();
    }

    @Override
    public Mono<User> getUserMono(String name) {
        return new R2DBCAdapter<User>("", TemplateType.DIR, "", null) {
            @Override
            protected User map(Map<String, Object> result) {
                return null;
            }
        }.query();
    }

    @Override
    public Mono<List<User>> getUserListMono(Sex sex, int age) {
        return new R2DBCAdapter<User>("", TemplateType.DIR, "", null) {
            @Override
            protected User map(Map<String, Object> result) {
                return null;
            }
        }.queryList();
    }

    @Override
    public Flux<User> getUserFlux(String name) {
        return new R2DBCAdapter<User>("", TemplateType.DIR, "", null) {
            @Override
            protected User map(Map<String, Object> result) {
                return null;
            }
        }.queryFlux();
    }
}
