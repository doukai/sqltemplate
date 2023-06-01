package io.sqltemplate.showcase.test.connection;

import com.google.auto.service.AutoService;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.sqltemplate.spi.connection.R2DBCConnectionProvider;
import org.mariadb.r2dbc.MariadbConnectionFactoryProvider;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@AutoService(R2DBCConnectionProvider.class)
public class MariaDBR2DBCConnectionProvider implements R2DBCConnectionProvider {

    public static ConnectionFactory connectionFactory = createConnectionFactory();

    private static ConnectionFactory createConnectionFactory() {
        InputStream resourceStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("r2dbc.properties");
        Properties r2dbcProperties = new Properties();
        try {
            r2dbcProperties.load(resourceStream);
            ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                    .option(ConnectionFactoryOptions.DRIVER, r2dbcProperties.getProperty("driver"))
                    .option(ConnectionFactoryOptions.PROTOCOL, r2dbcProperties.getProperty("protocol"))
                    .option(ConnectionFactoryOptions.HOST, r2dbcProperties.getProperty("host"))
                    .option(ConnectionFactoryOptions.PORT, Integer.parseInt(r2dbcProperties.getProperty("port")))
                    .option(ConnectionFactoryOptions.USER, r2dbcProperties.getProperty("user"))
                    .option(ConnectionFactoryOptions.PASSWORD, r2dbcProperties.getProperty("password"))
                    .option(ConnectionFactoryOptions.DATABASE, r2dbcProperties.getProperty("database"))
                    .option(MariadbConnectionFactoryProvider.ALLOW_MULTI_QUERIES, true)
                    .build();
            return ConnectionFactories.get(options);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Mono<Connection> createConnection() {
        return Mono.from(connectionFactory.create());
    }
}
