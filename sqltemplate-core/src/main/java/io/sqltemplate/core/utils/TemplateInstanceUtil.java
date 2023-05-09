package io.sqltemplate.core.utils;

import io.sqltemplate.spi.annotation.TemplateType;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupDir;
import org.stringtemplate.v4.STGroupFile;

import java.util.Map;

public enum TemplateInstanceUtil {
    TEMPLATE_INSTANCE_UTIL;

    public ST getInstance(String templateName, TemplateType type, String instanceName, Map<String, Object> params) {
        ST instance;
        if (type.equals(TemplateType.DIR)) {
            instance = new STGroupDir(templateName).getInstanceOf(instanceName);
        } else {
            instance = new STGroupFile(templateName).getInstanceOf(instanceName);
        }
        if (params != null) {
            params.forEach(instance::add);
        }
        return instance;
    }
}
