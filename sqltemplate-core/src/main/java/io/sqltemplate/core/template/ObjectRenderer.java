package io.sqltemplate.core.template;

import org.stringtemplate.v4.AttributeRenderer;

import java.util.Locale;
import java.util.Map;

public class ObjectRenderer implements AttributeRenderer<Object> {

    private final Map<String, Object> dbParamsMap;

    public ObjectRenderer(Map<String, Object> dbParamsMap) {
        this.dbParamsMap = dbParamsMap;
    }

    @Override
    public String toString(Object value, String formatString, Locale locale) {
        if (value instanceof Parameter) {
            String name = "p" + dbParamsMap.size();
            dbParamsMap.put(name, value);
            return ":" + name;
        }
        if (formatString != null) {
            if (formatString.equals(":")) {
                String name = "p" + dbParamsMap.size();
                dbParamsMap.put(name, value);
                return ":" + name;
            }
        }
        return value.toString();
    }
}
