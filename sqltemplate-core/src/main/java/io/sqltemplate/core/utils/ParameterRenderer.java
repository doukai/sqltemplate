package io.sqltemplate.core.utils;

import org.stringtemplate.v4.AttributeRenderer;

import java.util.List;
import java.util.Locale;

public class ParameterRenderer implements AttributeRenderer<Parameter> {

    private final List<String> parameterNameList;

    public ParameterRenderer(List<String> parameterNameList) {
        this.parameterNameList = parameterNameList;
    }

    @Override
    public String toString(Parameter value, String formatString, Locale locale) {
        parameterNameList.add(value.getName());
        return "?";
    }
}
