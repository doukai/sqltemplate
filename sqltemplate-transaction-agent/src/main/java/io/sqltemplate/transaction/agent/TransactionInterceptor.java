package io.sqltemplate.transaction.agent;

import io.sqltemplate.spi.transaction.JDBCTransactionManager;
import io.sqltemplate.spi.transaction.R2DBCTransactionManager;
import jakarta.transaction.Transactional;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class TransactionInterceptor {

    @RuntimeType
    public static Object intercept(@Origin Method method, @SuperCall Callable<?> callable) {
        Transactional transactional = method.getAnnotation(Transactional.class);
        if (method.getReturnType().isAssignableFrom(Mono.class)) {
            return Mono.usingWhen(
                    R2DBCTransactionManager.begin(transactional.value()),
                    tid -> {
                        try {
                            return (Mono<?>) callable.call();
                        } catch (Exception e) {
                            return R2DBCTransactionManager.rollback(tid, e, transactional.rollbackOn(), transactional.dontRollbackOn());
                        }
                    },
                    R2DBCTransactionManager::commit,
                    (tid, throwable) -> R2DBCTransactionManager.rollback(tid, throwable, transactional.rollbackOn(), transactional.dontRollbackOn()),
                    R2DBCTransactionManager::commit
            );
        } else if (method.getReturnType().isAssignableFrom(Flux.class)) {
            return Flux.usingWhen(
                    R2DBCTransactionManager.begin(transactional.value()),
                    tid -> {
                        try {
                            return (Flux<?>) callable.call();
                        } catch (Exception e) {
                            return R2DBCTransactionManager.rollback(tid, e, transactional.rollbackOn(), transactional.dontRollbackOn());
                        }
                    },
                    R2DBCTransactionManager::commit,
                    (tid, throwable) -> R2DBCTransactionManager.rollback(tid, throwable, transactional.rollbackOn(), transactional.dontRollbackOn()),
                    R2DBCTransactionManager::commit
            );
        } else {
            String tid = null;
            try {
                tid = JDBCTransactionManager.begin(transactional.value());
                Object called = callable.call();
                System.out.println(tid);
                JDBCTransactionManager.commit(tid);
                return called;
            } catch (Exception e) {
                JDBCTransactionManager.rollback(tid, e, transactional.rollbackOn(), transactional.dontRollbackOn());
                throw new RuntimeException(e);
            }
        }
    }
}
