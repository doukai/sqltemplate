package io.sqltemplate.showcase.templates;

import io.sqltemplate.spi.annotation.Instance;
import io.sqltemplate.spi.annotation.InstanceType;
import io.sqltemplate.spi.annotation.Template;
import jakarta.transaction.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.sql.SQLException;
import java.util.List;

@Template("stg/user")
public interface UserTemplate {

    @Transactional(value = Transactional.TxType.NEVER, rollbackOn = {ClassNotFoundException.class, SQLException.class}, dontRollbackOn = {RuntimeException.class})
    User getUser(String id) throws SQLException;

    @Instance(type = InstanceType.UPDATE)
    long updateUser(String id) throws SQLException;

    List<User> getUserList(String name) throws SQLException;

    Mono<User> getUserMono(String id);

    Mono<List<User>> getUserListMono(String name);

    Flux<User> getUserFlux(String name);
}
