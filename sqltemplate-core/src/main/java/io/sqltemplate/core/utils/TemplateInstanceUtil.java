package io.sqltemplate.core.utils;

import io.sqltemplate.core.expression.Expression;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupDir;
import org.stringtemplate.v4.STGroupFile;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public enum TemplateInstanceUtil {
    TEMPLATE_INSTANCE_UTIL;

    public ST getInstance(String templateName, String instanceName, Map<String, Object> paramsMap) {
        ST instance;
        if (templateName.endsWith(".stg")) {
            instance = new STGroupFile(templateName).getInstanceOf(instanceName);
        } else {
            if (templateName.startsWith("/")) {
                instance = new STGroupDir("").getInstanceOf(templateName.substring(1) + "/" + instanceName);
            } else if (!templateName.contains("/")) {
                instance = new STGroupDir("").getInstanceOf(templateName + "/" + instanceName);
            } else {
                String[] split = templateName.split("/");
                String dir = String.join("/", Arrays.copyOfRange(split, 0, split.length - 1));
                String file = split[split.length - 1];
                instance = new STGroupDir(dir).getInstanceOf(file + "/" + instanceName);
            }
        }
        if (paramsMap.keySet().stream().anyMatch(key -> instance.getAttributes().keySet().stream().noneMatch(attrName -> attrName.equals(key)))) {
            Object[] params = paramsMap.values().toArray();
            Iterator<String> iterator = instance.getAttributes().keySet().iterator();
            int index = 0;
            while (iterator.hasNext()) {
                instance.add(iterator.next(), Expression.of(params[index++]));
            }
        } else {
            paramsMap.forEach((key, value) -> instance.add(key, Expression.of(value)));
        }
        return instance;
    }
}
