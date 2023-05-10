package io.sqltemplate.core.r2dbc;

import com.google.common.base.CaseFormat;
import io.r2dbc.spi.Result;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.stringtemplate.v4.ST;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.sqltemplate.core.utils.TemplateInstanceUtil.TEMPLATE_INSTANCE_UTIL;

public abstract class R2DBCAdapter<T> {

    private final String templateName;

    private final String instanceName;

    private final Map<String, Object> paramsMap;

    private final ConnectionProvider connectionProvider;

    public R2DBCAdapter(String templateName, String instanceName, Map<String, Object> paramsMap) {
        this.templateName = templateName;
        this.instanceName = instanceName;
        this.paramsMap = paramsMap;
        this.connectionProvider = ConnectionProvider.provider();
    }

    public Mono<T> query() {
        ST instance = TEMPLATE_INSTANCE_UTIL.getInstance(templateName, instanceName, paramsMap);
        return connectionProvider.createConnection()
                .map(connection -> connection.createStatement(instance.render()))
                .flatMap(statement -> Mono.from(statement.execute()))
                .flatMap(this::getMapFormSegment)
                .map(this::map);
    }

    public Mono<List<T>> queryList() {
        ST instance = TEMPLATE_INSTANCE_UTIL.getInstance(templateName, instanceName, paramsMap);
        return connectionProvider.createConnection()
                .map(connection -> connection.createStatement(instance.render()))
                .flatMapMany(statement -> Flux.from(statement.execute()))
                .flatMap(this::getMapFormSegment)
                .collectList()
                .map(this::mapList);
    }

    public Flux<T> queryFlux() {
        ST instance = TEMPLATE_INSTANCE_UTIL.getInstance(templateName, instanceName, paramsMap);
        return connectionProvider.createConnection()
                .map(connection -> connection.createStatement(instance.render()))
                .flatMapMany(statement -> Flux.from(statement.execute()))
                .flatMap(this::getMapFormSegment)
                .map(this::map);
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

    protected abstract T map(Map<String, Object> result);

    private List<T> mapList(List<Map<String, Object>> list) {
        return list.stream().map(this::map).collect(Collectors.toList());
    }
}
