package io.sqltemplate.active.record;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.function.Supplier;

public interface ReactiveRecordIndex {

    Supplier<ReactiveRecord<?>> getRecordSupplier(String tableName);

    static ReactiveRecordIndex provider() {
        ServiceLoader<ReactiveRecordIndex> loader = ServiceLoader.load(ReactiveRecordIndex.class);
        Iterator<ReactiveRecordIndex> it = loader.iterator();
        if (it.hasNext()) {
            return it.next();
        }
        throw new RuntimeException("ReactiveRecordIndex undefined");
    }
}
