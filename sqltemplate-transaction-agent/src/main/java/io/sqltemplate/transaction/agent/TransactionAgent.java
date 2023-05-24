package io.sqltemplate.transaction.agent;

import jakarta.transaction.Transactional;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

public class TransactionAgent {

    private static final AgentBuilder agentBuilder = new AgentBuilder.Default();

    public static void premain(String agentArgs, Instrumentation inst) {
        install(inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        install(inst);
    }

    private static void install(Instrumentation inst) {
        agentBuilder
                .type(ElementMatchers.any())
                .transform((builder, type, classLoader, module, protectionDomain) ->
                        builder.method(ElementMatchers.isAnnotatedWith(Transactional.class))
                                .intercept(MethodDelegation.to(TransactionInterceptor.class)))
                .installOn(inst);
    }
}
