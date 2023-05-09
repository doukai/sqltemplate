package io.sqltemplate.showcase.templates;

import io.sqltemplate.spi.annotation.Template;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.util.List;

@Template("stg/user")
public interface UserTemplate {

    User getUser(String name) throws SQLException;

    List<User> getUserList(Sex sex, int age) throws SQLException;

    Mono<User> getUserMono(String name);

    Mono<List<User>> getUserListMono(Sex sex, int age);

    Flux<User> getUserFlux(String name);
}
