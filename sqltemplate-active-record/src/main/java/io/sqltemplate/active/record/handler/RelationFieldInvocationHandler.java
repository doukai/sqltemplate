package io.sqltemplate.active.record.handler;

import jakarta.persistence.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RelationFieldInvocationHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.isAnnotationPresent(OneToOne.class)) {

        } else if (method.isAnnotationPresent(OneToMany.class)) {

        } else if (method.isAnnotationPresent(ManyToOne.class)) {

        } else if (method.isAnnotationPresent(ManyToMany.class)) {

        }
        return method.invoke(proxy, args);
    }

    private JoinTable getJoinTable(Method method) {
        return method.getAnnotation(JoinTable.class);
    }

    private JoinColumn[] getJoinColumnList(Method method) {
        if (method.isAnnotationPresent(JoinColumns.class)) {
            return method.getAnnotation(JoinColumns.class).value();
        } else {
            return new JoinColumn[]{method.getAnnotation(JoinColumn.class)};
        }
    }
}
