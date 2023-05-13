package io.sqltemplate.core.r2dbc;

import io.r2dbc.spi.Connection;
import io.sqltemplate.spi.transaction.R2DBCConnectionCounter;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.TransactionRequiredException;
import jakarta.transaction.Transactional;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class R2DBCTransactionManager {

    private static final String connectionCounterListKey = "connectionCounterList";

    private final static R2DBCConnectionProvider connectionProvider = R2DBCConnectionProvider.provider();

    @SuppressWarnings("unchecked")
    public static Mono<List<R2DBCConnectionCounter>> getConnectionCounterList() {
        return Mono.empty()
                .transformDeferredContextual(
                        (mono, contextView) ->
                                Mono.justOrEmpty(
                                                contextView.getOrEmpty(connectionCounterListKey)
                                                        .map(connectionCounterList -> (List<R2DBCConnectionCounter>) connectionCounterList)
                                        )
                                        .switchIfEmpty(
                                                Objects.requireNonNull(connectionProvider).createConnection()
                                                        .map(R2DBCConnectionCounter::new)
                                                        .map(connectionCounter -> new ArrayList<R2DBCConnectionCounter>() {{
                                                                    add(connectionCounter);
                                                                }}
                                                        )
                                                        .flatMap(connectionCounterList ->
                                                                mono.thenReturn(connectionCounterList)
                                                                        .contextWrite(Context.of(connectionCounterListKey, connectionCounterList))

                                                        )
                                        )
                );
    }

    public static Mono<Connection> getConnection() {
        return getConnectionCounterList().map(connectionCounterList -> connectionCounterList.get(connectionCounterList.size() - 1)).map(R2DBCConnectionCounter::getConnection);
    }

    public static Mono<Void> begin(Transactional.TxType txType) {
        return getConnectionCounterList()
                .flatMap(connectionCounterList -> {
                            R2DBCConnectionCounter connectionCounter = connectionCounterList.get(connectionCounterList.size() - 1);
                            switch (txType) {
                                case REQUIRED:
                                    if (connectionCounter.getCounter().getAndIncrement() == 0) {
                                        return Mono.from(connectionCounter.getConnection().setAutoCommit(false))
                                                .then(Mono.from(connectionCounter.getConnection().beginTransaction()));
                                    } else {
                                        return Mono.empty();
                                    }
                                case REQUIRES_NEW:
                                    return Objects.requireNonNull(connectionProvider).createConnection()
                                            .flatMap(connection -> {
                                                        R2DBCConnectionCounter newConnectionCounter = new R2DBCConnectionCounter(connection);
                                                        connectionCounterList.add(newConnectionCounter);
                                                        newConnectionCounter.getCounter().incrementAndGet();
                                                        return Mono.from(connectionCounter.getConnection().setAutoCommit(false))
                                                                .then(Mono.from(connectionCounter.getConnection().beginTransaction()));
                                                    }
                                            );
                                case MANDATORY:
                                    if (connectionCounter.getCounter().getAndIncrement() == 0) {
                                        return Mono.error(new TransactionRequiredException());
                                    } else {
                                        return Mono.empty();
                                    }
                                case SUPPORTS:
                                    return Mono.empty();
                                case NOT_SUPPORTED:
                                    return Objects.requireNonNull(connectionProvider).createConnection()
                                            .flatMap(connection -> {
                                                        R2DBCConnectionCounter newConnectionCounter = new R2DBCConnectionCounter(connection);
                                                        connectionCounterList.add(newConnectionCounter);
                                                        return Mono.empty();
                                                    }
                                            );
                                case NEVER:
                                    if (connectionCounter.getCounter().getAndIncrement() == 0) {
                                        return Mono.empty();
                                    } else {
                                        return Mono.error(new InvalidTransactionException());
                                    }
                                default:
                                    return Mono.error(new NotSupportedException());
                            }
                        }
                );
    }

    public static Mono<Void> commit() {
        return getConnectionCounterList()
                .flatMap(connectionCounterList -> {
                            R2DBCConnectionCounter connectionCounter = connectionCounterList.get(connectionCounterList.size() - 1);
                            if (connectionCounter.getConnection().isAutoCommit()) {
                                connectionCounterList.remove(connectionCounter);
                                return Mono.from(connectionCounter.getConnection().close());
                            } else if (connectionCounter.getCounter().getAndDecrement() == 1) {
                                connectionCounterList.remove(connectionCounter);
                                return Mono.from(connectionCounter.getConnection().commitTransaction())
                                        .then(Mono.from(connectionCounter.getConnection().close()));
                            } else {
                                connectionCounter.getCounter().decrementAndGet();
                                return Mono.empty();
                            }
                        }
                );
    }

    public static Mono<Void> rollback(Throwable throwable, Class<?>[] rollbackOn, Class<?>[] dontRollbackOn) {
        return getConnectionCounterList()
                .flatMap(connectionCounterList -> {
                            R2DBCConnectionCounter connectionCounter = connectionCounterList.get(connectionCounterList.size() - 1);
                            connectionCounterList.remove(connectionCounter);
                            if (rollbackOn != null && rollbackOn.length > 0) {
                                if (Arrays.stream(rollbackOn).anyMatch(exception -> exception.equals(throwable.getClass()))) {
                                    return Mono.from(connectionCounter.getConnection().rollbackTransaction())
                                            .then(Mono.from(connectionCounter.getConnection().close()));
                                } else {
                                    return Mono.from(connectionCounter.getConnection().commitTransaction())
                                            .then(Mono.from(connectionCounter.getConnection().close()));
                                }
                            } else if (dontRollbackOn != null && dontRollbackOn.length > 0) {
                                if (Arrays.stream(dontRollbackOn).anyMatch(exception -> exception.equals(throwable.getClass()))) {
                                    return Mono.from(connectionCounter.getConnection().commitTransaction())
                                            .then(Mono.from(connectionCounter.getConnection().close()));
                                } else {
                                    return Mono.from(connectionCounter.getConnection().rollbackTransaction())
                                            .then(Mono.from(connectionCounter.getConnection().close()));
                                }
                            }
                            return Mono.from(connectionCounter.getConnection().rollbackTransaction())
                                    .then(Mono.from(connectionCounter.getConnection().close()));
                        }
                );
    }
}
