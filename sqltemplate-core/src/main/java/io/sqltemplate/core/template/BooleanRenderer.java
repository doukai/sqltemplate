package io.sqltemplate.core.template;

import org.stringtemplate.v4.AttributeRenderer;

import java.util.Locale;
import java.util.Map;

public class BooleanRenderer implements AttributeRenderer<Boolean> {

    private final Map<String, Object> dbParamsMap;

    public BooleanRenderer(Map<String, Object> dbParamsMap) {
        this.dbParamsMap = dbParamsMap;
    }

    @Override
    public String toString(Boolean value, String formatString, Locale locale) {
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
