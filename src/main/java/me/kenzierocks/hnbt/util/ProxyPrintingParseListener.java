package me.kenzierocks.hnbt.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.antlr.v4.runtime.tree.ParseTreeListener;

public final class ProxyPrintingParseListener {

    public static <T extends ParseTreeListener> T
            create(Class<? extends T> iface) {
        return iface.cast(Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[] { iface }, new InvocationHandler() {

                    @Override
                    public Object invoke(Object proxy, Method method,
                            Object[] args) throws Throwable {
                        System.err.println(String.format(
                                "ParseTreeListener.%s(%s)", method.getName(),
                                Stream.of(args).map(String::valueOf)
                                        .collect(Collectors.joining(", "))));
                        return null;
                    }
                }));
    }

    private ProxyPrintingParseListener() {
        throw new AssertionError();
    }

}
