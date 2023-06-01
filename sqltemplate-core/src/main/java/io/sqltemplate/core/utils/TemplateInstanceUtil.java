package io.sqltemplate.core.utils;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupDir;
import org.stringtemplate.v4.STGroupFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum TemplateInstanceUtil {
    TEMPLATE_INSTANCE_UTIL;

    public String getSQLWithParams(String templateName, String instanceName, Map<String, Object> paramsMap) {
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
        Map<String, Object> dbParamsMap = new HashMap<>();
        ParameterRenderer parameterRenderer = new ParameterRenderer(dbParamsMap);
        stGroup.registerRenderer(Parameter.class, parameterRenderer);
        List<String> attributeKeyList = new ArrayList<>(instance.impl.formalArguments.keySet());
        if (paramsMap.keySet().stream().anyMatch(key -> attributeKeyList.stream().noneMatch(attrName -> attrName.equals(key)))) {
            Map<String, Object> namedParamsMap = new HashMap<>();
            List<Object> params = new ArrayList<>(paramsMap.values());
            for (int index = 0; index < attributeKeyList.size(); index++) {
                String key = attributeKeyList.get(index);
                namedParamsMap.put(key, params.get(index));
            }
            paramsMap.clear();
            paramsMap.putAll(namedParamsMap);
        }
        paramsMap.forEach(instance::add);
        String sql = instance.render();
        if (dbParamsMap.size() > 0) {
            paramsMap.putAll(dbParamsMap);
        }
        return sql;
    }
}
