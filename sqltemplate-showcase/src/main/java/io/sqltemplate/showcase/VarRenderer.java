package io.sqltemplate.showcase;

import org.stringtemplate.v4.AttributeRenderer;

import java.util.List;
import java.util.Locale;

public class VarRenderer implements AttributeRenderer<Var> {
    List<String> varNameList;

    public VarRenderer(List<String> varNameList) {
        this.varNameList = varNameList;
    }

    @Override
    public String toString(Var value, String formatString, Locale locale) {
        varNameList.add(value.getName());
        return varNameList.size() + "";
    }
}
