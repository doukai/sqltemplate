package io.sqltemplate.core.template;

import org.stringtemplate.v4.AttributeRenderer;

import java.util.Locale;
import java.util.Map;

public class StringRenderer implements AttributeRenderer<String> {

    private final Map<String, Object> dbParamsMap;

    public StringRenderer(Map<String, Object> dbParamsMap) {
        this.dbParamsMap = dbParamsMap;
    }

    @Override
    public String toString(String value, String formatString, Locale locale) {
        if (formatString != null) {
            if (formatString.equals(":")) {
                String name = "p" + dbParamsMap.size();
                dbParamsMap.put(name, value);
                return ":" + name;
            }
        }
        return value;
    }
}
