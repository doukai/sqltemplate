package io.sqltemplate.active.record;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.function.Supplier;

public interface RecordIndex {

    Supplier<Record<?>> getRecordSupplier(String tableName);

    static RecordIndex provider() {
        ServiceLoader<RecordIndex> loader = ServiceLoader.load(RecordIndex.class);
        Iterator<RecordIndex> it = loader.iterator();
        if (it.hasNext()) {
            return it.next();
        }
        throw new RuntimeException("RecordIndex undefined");
    }
}
