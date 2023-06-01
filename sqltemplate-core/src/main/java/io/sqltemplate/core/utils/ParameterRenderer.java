package io.sqltemplate.core.utils;

import org.stringtemplate.v4.AttributeRenderer;

import java.util.Locale;
import java.util.Map;

public class ParameterRenderer implements AttributeRenderer<Parameter> {

    private final Map<String, Object> dbParamsMap;

    public ParameterRenderer(Map<String, Object> dbParamsMap) {
        this.dbParamsMap = dbParamsMap;
    }

    @Override
    public String toString(Parameter value, String formatString, Locale locale) {
        String name = "p" + dbParamsMap.size();
        dbParamsMap.put(name, value);
        return ":" + name;
    }
}
