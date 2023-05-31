package io.sqltemplate.core.utils;

import io.sqltemplate.core.expression.Parameter;
import org.stringtemplate.v4.AttributeRenderer;

import java.util.List;
import java.util.Locale;

public class ObjectRenderer implements AttributeRenderer<Object> {

    private final List<Object> attributeNameList;

    public ObjectRenderer(List<Object> attributeNameList) {
        this.attributeNameList = attributeNameList;
    }

    @Override
    public String toString(Object value, String formatString, Locale locale) {
        if (value instanceof Parameter) {
            attributeNameList.add(((Parameter) value).getValue());
            return "?";
        }
        if (formatString != null) {
            if (formatString.equals("?")) {
                attributeNameList.add(value);
                return formatString;
            }
        }
        return value.toString();
    }
}
