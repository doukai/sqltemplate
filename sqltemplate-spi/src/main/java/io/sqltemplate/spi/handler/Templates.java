package io.sqltemplate.spi.handler;

public class Templates {

    public static <T> T get(Class<T> tClass) {
        return getTemplateProvider().getTemplate(tClass);
    }

    private static class TemplateProviderHolder {
        private static final TemplateProvider INSTANCE = TemplateProvider.provider();
    }

    public static TemplateProvider getTemplateProvider() {
        return TemplateProviderHolder.INSTANCE;
    }
}
