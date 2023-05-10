package io.sqltemplate.showcase.templates;

import io.sqltemplate.spi.annotation.Template;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.util.List;

@Template("stg/user")
public interface UserTemplate {

    User getUser(String id) throws SQLException;

    List<User> getUserList(String name) throws SQLException;

    Mono<User> getUserMono(String id);

    Mono<List<User>> getUserListMono(String name);

    Flux<User> getUserFlux(String name);
}
