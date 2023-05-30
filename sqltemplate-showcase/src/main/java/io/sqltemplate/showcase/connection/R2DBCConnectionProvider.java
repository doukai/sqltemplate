package io.sqltemplate.showcase.connection;

import com.google.auto.service.AutoService;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.mariadb.r2dbc.MariadbConnectionFactoryProvider;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@AutoService(io.sqltemplate.spi.connection.R2DBCConnectionProvider.class)
public class R2DBCConnectionProvider implements io.sqltemplate.spi.connection.R2DBCConnectionProvider {
    @Override
    public Mono<Connection> createConnection() {
        InputStream resourceStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("r2dbc.properties");
        Properties r2dbcProperties = new Properties();
        try {
            r2dbcProperties.load(resourceStream);
            ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                    .option(ConnectionFactoryOptions.DRIVER, r2dbcProperties.getProperty("driver"))
                    .option(ConnectionFactoryOptions.PROTOCOL, r2dbcProperties.getProperty("protocol"))
                    .option(ConnectionFactoryOptions.HOST, r2dbcProperties.getProperty("host"))
                    .option(ConnectionFactoryOptions.PORT, (int) r2dbcProperties.get("port"))
                    .option(ConnectionFactoryOptions.USER, r2dbcProperties.getProperty("user"))
                    .option(ConnectionFactoryOptions.PASSWORD, r2dbcProperties.getProperty("password"))
                    .option(ConnectionFactoryOptions.DATABASE, r2dbcProperties.getProperty("database"))
                    .option(MariadbConnectionFactoryProvider.ALLOW_MULTI_QUERIES, true)
                    .build();
            return Mono.from(ConnectionFactories.get(options).create());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
