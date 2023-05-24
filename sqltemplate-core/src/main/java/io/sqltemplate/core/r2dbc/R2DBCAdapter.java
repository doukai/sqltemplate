package io.sqltemplate.core.r2dbc;

import com.google.common.base.CaseFormat;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import io.sqltemplate.core.adapter.Adapter;
import io.sqltemplate.spi.transaction.R2DBCTransactionManager;
import jakarta.transaction.Transactional;
import org.stringtemplate.v4.ST;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.sqltemplate.core.utils.TemplateInstanceUtil.TEMPLATE_INSTANCE_UTIL;

public class R2DBCAdapter<T> extends Adapter<T> {

    public R2DBCAdapter() {
    }

    public R2DBCAdapter(String templateName, String instanceName, Map<String, Object> params) {
        super(templateName, instanceName, params);
    }

    public R2DBCAdapter(String templateName, String instanceName, Map<String, Object> params, Transactional.TxType txType, Class<?>[] rollbackOn, Class<?>[] dontRollbackOn) {
        super(templateName, instanceName, params, txType, rollbackOn, dontRollbackOn);
    }

    public R2DBCAdapter(String templateName, String instanceName, Map<String, Object> params, Transactional transactional) {
        super(templateName, instanceName, params, transactional);
    }

    public Mono<T> query() {
        ST instance = TEMPLATE_INSTANCE_UTIL.getInstance(getTemplateName(), getInstanceName(), getParams());
        return Mono.usingWhen(
                        R2DBCTransactionManager.begin(getTxType()),
                        tid -> R2DBCTransactionManager.getConnection().flatMap(connection -> Mono.from(connection.createStatement(instance.render()).execute())),
                        R2DBCTransactionManager::commit,
                        (tid, throwable) -> R2DBCTransactionManager.rollback(tid, throwable, getRollbackOn(), getDontRollbackOn()),
                        R2DBCTransactionManager::commit
                )
                .flatMap(this::getMapFormSegment)
                .map(this::map);
    }

    public Mono<List<T>> queryList() {
        ST instance = TEMPLATE_INSTANCE_UTIL.getInstance(getTemplateName(), getInstanceName(), getParams());
        return Flux.usingWhen(
                        R2DBCTransactionManager.begin(getTxType()),
                        tid -> R2DBCTransactionManager.getConnection().flatMap(connection -> Mono.from(connection.createStatement(instance.render()).execute())),
                        R2DBCTransactionManager::commit,
                        (tid, throwable) -> R2DBCTransactionManager.rollback(tid, throwable, getRollbackOn(), getDontRollbackOn()),
                        R2DBCTransactionManager::commit
                )
                .flatMap(this::getMapFormSegment)
                .collectList()
                .map(this::mapList);
    }

    public Flux<T> queryFlux() {
        ST instance = TEMPLATE_INSTANCE_UTIL.getInstance(getTemplateName(), getInstanceName(), getParams());
        return Flux.usingWhen(
                        R2DBCTransactionManager.begin(getTxType()),
                        tid -> R2DBCTransactionManager.getConnection().flatMap(connection -> Mono.from(connection.createStatement(instance.render()).execute())),
                        R2DBCTransactionManager::commit,
                        (tid, throwable) -> R2DBCTransactionManager.rollback(tid, throwable, getRollbackOn(), getDontRollbackOn()),
                        R2DBCTransactionManager::commit
                )
                .flatMap(this::getMapFormSegment)
                .map(this::map);
    }

    public Mono<Long> update() {
        ST instance = TEMPLATE_INSTANCE_UTIL.getInstance(getTemplateName(), getInstanceName(), getParams());
        return Mono.usingWhen(
                        R2DBCTransactionManager.begin(getTxType()),
                        tid -> R2DBCTransactionManager.getConnection().flatMap(connection -> Mono.from(connection.createStatement(instance.render()).execute())),
                        R2DBCTransactionManager::commit,
                        (tid, throwable) -> R2DBCTransactionManager.rollback(tid, throwable, getRollbackOn(), getDontRollbackOn()),
                        R2DBCTransactionManager::commit
                )
                .flatMap(this::getUpdateCountFromResult);
    }

    private Mono<Map<String, Object>> getMapFormSegment(Result result) {
        return Mono.from(result.flatMap(this::getMapFormSegment));
    }

    private Mono<Long> getUpdateCountFromResult(Result result) {
        return Mono.from(result.flatMap(this::getUpdateCountFormSegment));
    }

    private Mono<Map<String, Object>> getMapFormSegment(Result.Segment segment) {
        if (segment instanceof Result.Message) {
            return Mono.error(((Result.Message) segment).exception());
        } else if (segment instanceof Result.RowSegment) {
            Result.RowSegment rowSegment = (Result.RowSegment) segment;
            Row row = rowSegment.row();
            RowMetadata metadata = row.getMetadata();
            return Mono.just(
                    metadata.getColumnMetadatas().stream()
                            .map(columnMetadata -> new AbstractMap.SimpleEntry<>(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnMetadata.getName()), row.get(columnMetadata.getName())))
                            .collect(Collectors.toMap(Map.Entry<String, Object>::getKey, Map.Entry<String, Object>::getValue))
            );
        } else {
            return Mono.empty();
        }
    }

    private Mono<Long> getUpdateCountFormSegment(Result.Segment segment) {
        if (segment instanceof Result.Message) {
            return Mono.error(((Result.Message) segment).exception());
        } else if (segment instanceof Result.UpdateCount) {
            return Mono.just(((Result.UpdateCount) segment).value());
        } else {
            return Mono.empty();
        }
    }
}
