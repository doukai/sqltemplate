package io.sqltemplate.spi.handler;

public class Templates {

    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> tClass) {
        try {
            return (T) Class.forName(tClass.getName().concat("Impl"));
        } catch (ClassNotFoundException ignored) {
        }
        return null;
    }
}
