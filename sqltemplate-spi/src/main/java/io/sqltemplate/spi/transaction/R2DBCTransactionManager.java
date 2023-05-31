package io.sqltemplate.spi.transaction;

import io.r2dbc.spi.Connection;
import io.sqltemplate.spi.connection.R2DBCConnectionProvider;
import jakarta.transaction.InvalidTransactionException;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.TransactionRequiredException;
import jakarta.transaction.Transactional;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static jakarta.transaction.Transactional.TxType.REQUIRED;

public class R2DBCTransactionManager {

    private static final String transactionConnectionListKey = "transactionConnectionList";

    private final static R2DBCConnectionProvider connectionProvider = R2DBCConnectionProvider.provider();

    @SuppressWarnings("unchecked")
    public static Mono<List<R2DBCTransactionConnection>> getTransactionConnectionList() {
        return Mono.deferContextual(contextView ->
                Mono.justOrEmpty(contextView.getOrEmpty(transactionConnectionListKey))
                        .map(connectionCounterList -> (List<R2DBCTransactionConnection>) connectionCounterList)
        );
    }

    public static Context init(Context context) {
        if (context.hasKey(transactionConnectionListKey)) {
            return context;
        }
        return Context.of(transactionConnectionListKey, new ArrayList<R2DBCTransactionConnection>());
    }

    public static Mono<R2DBCTransactionConnection> getTransactionConnection() {
        return getTransactionConnectionList().map(connectionCounterList -> connectionCounterList.get(connectionCounterList.size() - 1));
    }

    public static Mono<Connection> getConnection() {
        return getTransactionConnection().map(R2DBCTransactionConnection::getConnection);
    }

    public static Mono<String> begin() {
        return begin(REQUIRED);
    }

    public static Mono<String> begin(Transactional.TxType txType) {
        return getTransactionConnectionList()
                .flatMap(transactionConnectionList -> {
                            String id = UUID.randomUUID().toString();
                            switch (txType) {
                                case REQUIRED:
                                    if (transactionConnectionList.size() == 0) {
                                        return connectionProvider.createConnection()
                                                .flatMap(connection ->
                                                        Mono.from(connection.setAutoCommit(false))
                                                                .then(Mono.from(connection.beginTransaction()))
                                                                .thenReturn(connection)
                                                )
                                                .doOnSuccess(connection -> transactionConnectionList.add(new R2DBCTransactionConnection(id, connection)))
                                                .thenReturn(id);
                                    } else {
                                        return Mono.just(id);
                                    }
                                case REQUIRES_NEW:
                                    return connectionProvider.createConnection()
                                            .flatMap(connection ->
                                                    Mono.from(connection.setAutoCommit(false))
                                                            .then(Mono.from(connection.beginTransaction()))
                                                            .thenReturn(connection)
                                            )
                                            .doOnSuccess(connection -> transactionConnectionList.add(new R2DBCTransactionConnection(id, connection)))
                                            .thenReturn(id);
                                case MANDATORY:
                                    if (transactionConnectionList.size() == 0) {
                                        return Mono.error(new TransactionRequiredException());
                                    } else {
                                        return Mono.just(id);
                                    }
                                case SUPPORTS:
                                    if (transactionConnectionList.size() == 0) {
                                        return connectionProvider.createConnection()
                                                .doOnSuccess(connection -> transactionConnectionList.add(new R2DBCTransactionConnection(id, connection)))
                                                .thenReturn(id);
                                    } else {
                                        return Mono.just(id);
                                    }
                                case NOT_SUPPORTED:
                                    return connectionProvider.createConnection()
                                            .doOnSuccess(connection -> transactionConnectionList.add(new R2DBCTransactionConnection(id, connection)))
                                            .thenReturn(id);
                                case NEVER:
                                    if (transactionConnectionList.size() == 0) {
                                        return connectionProvider.createConnection()
                                                .doOnSuccess(connection -> transactionConnectionList.add(new R2DBCTransactionConnection(id, connection)))
                                                .thenReturn(id);
                                    } else {
                                        return Mono.error(new InvalidTransactionException());
                                    }
                                default:
                                    return Mono.error(new NotSupportedException());
                            }
                        }
                );
    }

    public static Mono<Void> commit(String id) {
        return getTransactionConnectionList()
                .flatMap(transactionConnectionList -> {
                            R2DBCTransactionConnection transactionConnection = transactionConnectionList.get(transactionConnectionList.size() - 1);
                            if (transactionConnection.getConnection().isAutoCommit()) {
                                return Mono.from(transactionConnection.getConnection().close())
                                        .doOnSuccess(v -> transactionConnectionList.remove(transactionConnection));
                            } else {
                                if (transactionConnection.getId().equals(id)) {
                                    return Mono.from(transactionConnection.getConnection().commitTransaction())
                                            .then(Mono.from(transactionConnection.getConnection().close()))
                                            .doOnSuccess(v -> transactionConnectionList.remove(transactionConnection));
                                }
                                return Mono.empty();
                            }
                        }
                );
    }

    public static Mono<Void> rollback(String id) {
        return getTransactionConnectionList()
                .flatMap(transactionConnectionList -> {
                            R2DBCTransactionConnection transactionConnection = transactionConnectionList.get(transactionConnectionList.size() - 1);
                            if (transactionConnection.getConnection().isAutoCommit()) {
                                return Mono.from(transactionConnection.getConnection().close())
                                        .doOnSuccess(v -> transactionConnectionList.remove(transactionConnection));
                            } else {
                                if (transactionConnection.getId().equals(id)) {
                                    return Mono.from(transactionConnection.getConnection().rollbackTransaction())
                                            .then(Mono.from(transactionConnection.getConnection().close()))
                                            .doOnSuccess(v -> transactionConnectionList.remove(transactionConnection));
                                }
                                return Mono.empty();
                            }
                        }
                );
    }

    public static Mono<Void> rollback(String id, Throwable throwable, Class<?>[] rollbackOn, Class<?>[] dontRollbackOn) {
        return getTransactionConnectionList()
                .flatMap(transactionConnectionList -> {
                            if (transactionConnectionList.size() == 0) {
                                return Mono.error(throwable);
                            }
                            R2DBCTransactionConnection transactionConnection = transactionConnectionList.get(transactionConnectionList.size() - 1);
                            if (rollbackOn != null && rollbackOn.length > 0) {
                                if (Arrays.stream(rollbackOn).anyMatch(exception -> exception.equals(throwable.getClass()))) {
                                    return Mono.from(transactionConnection.getConnection().rollbackTransaction())
                                            .then(Mono.from(transactionConnection.getConnection().close()))
                                            .doOnSuccess(v -> transactionConnectionList.remove(transactionConnection))
                                            .then(Mono.error(throwable));
                                } else {
                                    return commit(id);
                                }
                            } else if (dontRollbackOn != null && dontRollbackOn.length > 0) {
                                if (Arrays.stream(dontRollbackOn).anyMatch(exception -> exception.equals(throwable.getClass()))) {
                                    return commit(id);
                                } else {
                                    return Mono.from(transactionConnection.getConnection().rollbackTransaction())
                                            .then(Mono.from(transactionConnection.getConnection().close()))
                                            .doOnSuccess(v -> transactionConnectionList.remove(transactionConnection))
                                            .then(Mono.error(throwable));
                                }
                            }
                            return Mono.from(transactionConnection.getConnection().rollbackTransaction())
                                    .then(Mono.from(transactionConnection.getConnection().close()))
                                    .doOnSuccess(v -> transactionConnectionList.remove(transactionConnection))
                                    .then(Mono.error(throwable));
                        }
                );
    }
}
