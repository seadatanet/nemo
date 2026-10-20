package fr.ifremer.nemo.ws.common;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;

public class ProxyUtil {

    public static <T> T newLazyInitializingProxy(Class<T> clazz, Callable<T> callable) {

        InvocationHandler ih = new InvocationHandler() {

            private T instanciated = null;

            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                if(instanciated == null) {
                    instanciated = callable.call();
                }
                return method.invoke(instanciated, objects);
            }
        };

        return (T)Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, ih);
    }
}
