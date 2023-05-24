package io.sqltemplate.transaction.agent;

import jakarta.transaction.Transactional;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

public class TransactionAgent {

    public static List<String> interceptClassNameList = new ArrayList<>();

    public static void premain(String agentArgs, Instrumentation inst) {
        new AgentBuilder.Default()
                .type(ElementMatchers.named("io.sqltemplate.showcase.Application"))
                .transform((builder, type, classLoader, module, protectionDomain) ->
                        builder.method(ElementMatchers.named("testInsert").and(ElementMatchers.isAnnotatedWith(Transactional.class)))
                                .intercept(MethodDelegation.to(TransactionInterceptor.class))
                )
                .installOn(inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        new AgentBuilder.Default()
                .type(ElementMatchers.any())
                .transform((builder, type, classLoader, module, protectionDomain) ->
                        builder.method(ElementMatchers.named("testInsert").and(ElementMatchers.isAnnotatedWith(Transactional.class)))
                                .intercept(MethodDelegation.to(TransactionInterceptor.class)))
                .installOn(inst);
    }
}
