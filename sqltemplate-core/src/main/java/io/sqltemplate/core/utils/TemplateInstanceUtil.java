package io.sqltemplate.core.utils;

import io.sqltemplate.core.expression.Expression;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;
import org.stringtemplate.v4.STGroupFile;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public enum TemplateInstanceUtil {
    TEMPLATE_INSTANCE_UTIL;

    public Map.Entry<String, Map<Integer, Object>> getInstance(String templateName, String instanceName, Map<String, Object> paramsMap) {
        STGroup stGroup;
        ST instance;
        if (templateName.endsWith(".stg")) {
            stGroup = new STGroupFile(templateName);
            instance = stGroup.getInstanceOf(instanceName);
        } else {
            if (templateName.startsWith("/")) {
                stGroup = new STGroupDir("");
                instance = stGroup.getInstanceOf(templateName.substring(1) + "/" + instanceName);
            } else if (!templateName.contains("/")) {
                stGroup = new STGroupDir("");
                instance = stGroup.getInstanceOf(templateName + "/" + instanceName);
            } else {
                String[] split = templateName.split("/");
                String dir = String.join("/", Arrays.copyOfRange(split, 0, split.length - 1));
                stGroup = new STGroupDir(dir);
                String file = split[split.length - 1];
                instance = stGroup.getInstanceOf(file + "/" + instanceName);
            }
        }
        List<String> parameterNameList = new ArrayList<>();
        ParameterRenderer parameterRenderer = new ParameterRenderer(parameterNameList);
        stGroup.registerRenderer(Parameter.class, parameterRenderer);
        String[] attributeKeys = instance.getAttributes().keySet().toArray(new String[]{});
        if (paramsMap.keySet().stream().anyMatch(key -> instance.getAttributes().keySet().stream().noneMatch(attrName -> attrName.equals(key)))) {
            Object[] params = paramsMap.values().toArray();
            for (int index = 0; index < attributeKeys.length; index++) {
                String key = attributeKeys[index];
                if (key.startsWith("_")) {
                    instance.add(key, new Parameter(key));
                } else {
                    instance.add(key, Expression.of(params[index]));
                }
            }
        } else {
            for (String key : attributeKeys) {
                if (key.startsWith("_")) {
                    instance.add(key, new Parameter(key));
                } else {
                    instance.add(key, Expression.of(paramsMap.get(key)));
                }
            }
        }
        Map<Integer, Object> dbParamsMap = IntStream.range(0, parameterNameList.size())
                .mapToObj(index -> new AbstractMap.SimpleEntry<>(index, paramsMap.get(parameterNameList.get(index))))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return new AbstractMap.SimpleEntry<>(instance.render(), dbParamsMap);
    }
}
