package io.sqltemplate.showcase.test.templates;

import io.sqltemplate.showcase.test.dto.User;
import io.sqltemplate.spi.annotation.Instance;
import io.sqltemplate.spi.annotation.InstanceType;
import io.sqltemplate.spi.annotation.Template;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Template("stg/user")
public interface UserTemplate {

    User getUser(String id);

    List<User> getUserListByName(String name);

    @Instance(type = InstanceType.UPDATE)
    long insertUser(int id, String name, String login, String password, int age);

    @Instance(type = InstanceType.UPDATE)
    long updateUserNameById(String id, String name);

    @Instance("getUser")
    Mono<User> getUserByNameMono(String id);

    @Instance("getUserListByName")
    Mono<List<User>> getUserListByNameMono(String name);

    @Instance("getUserListByName")
    Flux<User> getUserByNameFlux(String name);

    @Instance(value = "insertUser", type = InstanceType.UPDATE)
    Mono<Long> insertUserMono(int id, String name, String login, String password, int age);

    @Instance(value = "updateUserNameById", type = InstanceType.UPDATE)
    Mono<Long> updateUserNameByIdMono(String id, String name);
}
