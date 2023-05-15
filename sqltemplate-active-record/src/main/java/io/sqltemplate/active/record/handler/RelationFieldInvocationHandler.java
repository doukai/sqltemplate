package io.sqltemplate.active.record.handler;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

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
        return null;
    }
}
