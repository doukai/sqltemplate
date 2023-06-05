package io.sqltemplate.core.template;

import org.stringtemplate.v4.AttributeRenderer;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import java.util.Map;

public class TemporalAccessorRenderer implements AttributeRenderer<TemporalAccessor> {

    private final Map<String, Object> dbParamsMap;

    public TemporalAccessorRenderer(Map<String, Object> dbParamsMap) {
        this.dbParamsMap = dbParamsMap;
    }

    @Override
    public String toString(TemporalAccessor value, String formatString, Locale locale) {
        if (formatString != null) {
            if (formatString.equals(":")) {
                String name = "p" + dbParamsMap.size();
                dbParamsMap.put(name, value);
                return ":" + name;
            } else {
                return DateTimeFormatter.ofPattern(formatString, locale != null ? locale : Locale.getDefault()).format(value);
            }
        }
        return value.toString();
    }
}
