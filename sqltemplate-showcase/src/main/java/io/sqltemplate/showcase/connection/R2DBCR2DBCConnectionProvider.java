package io.sqltemplate.showcase.connection;

import com.google.auto.service.AutoService;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.mariadb.r2dbc.MariadbConnectionFactoryProvider;
import reactor.core.publisher.Mono;

@AutoService(io.sqltemplate.core.r2dbc.R2DBCConnectionProvider.class)
public class R2DBCR2DBCConnectionProvider implements io.sqltemplate.core.r2dbc.R2DBCConnectionProvider {
    @Override
    public Mono<Connection> createConnection() {
        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                .option(ConnectionFactoryOptions.DRIVER, "mariadb")
                .option(ConnectionFactoryOptions.PROTOCOL, "pipes")
                .option(ConnectionFactoryOptions.HOST, "localhost")
                .option(ConnectionFactoryOptions.PORT, 3306)
                .option(ConnectionFactoryOptions.USER, "root")
                .option(ConnectionFactoryOptions.PASSWORD, "root")
                .option(ConnectionFactoryOptions.DATABASE, "user")
                .option(MariadbConnectionFactoryProvider.ALLOW_MULTI_QUERIES, true)
                .build();
        return Mono.from(ConnectionFactories.get(options).create());
    }
}
