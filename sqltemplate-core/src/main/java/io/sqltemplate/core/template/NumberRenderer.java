package io.sqltemplate.core.template;

import org.stringtemplate.v4.AttributeRenderer;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

public class NumberRenderer implements AttributeRenderer<Number> {

    private final Map<String, Object> dbParamsMap;

    public NumberRenderer(Map<String, Object> dbParamsMap) {
        this.dbParamsMap = dbParamsMap;
    }

    @Override
    public String toString(Number value, String formatString, Locale locale) {
        if (formatString != null) {
            if (formatString.equals(":")) {
                String name = "p" + dbParamsMap.size();
                dbParamsMap.put(name, value);
                return ":" + name;
            } else {
                return NumberFormat.getCurrencyInstance(locale != null ? locale : Locale.getDefault()).format(value);
            }
        }
        return value.toString();
    }
}
