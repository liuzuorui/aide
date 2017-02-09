//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.aide.integration.proxy;

import com.aide.core.AideException;
import com.aide.core.ServiceProvider;
import com.aide.core.proxy.JavaService;
import com.aide.core.proxy.JavaServiceProvider;
import com.aide.core.proxy.JavaServiceProxy;
import com.aide.core.registry.ServiceResolver;
import com.aide.core.selector.ServiceProviderSelector;
import com.google.gson.GsonBuilder;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AideServiceInvocationHandler implements InvocationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AideServiceInvocationHandler.class);
    private static AtomicLong INDEX = new AtomicLong(0L);
    private final Class<?> targetClass;
    private final String serviceName;
    private final ServiceResolver serviceResolver;
    private final JavaServiceProxy serviceProxy;
    private final ServiceProviderSelector serviceProviderSelector;

    AideServiceInvocationHandler(Class<?> targetClass, String serviceName, JavaServiceProxy serviceProxy, ServiceResolver serviceResolver, ServiceProviderSelector serviceProviderSelector) {
        this.targetClass = targetClass;
        this.serviceName = removeAsyncSuffix(serviceName);
        this.serviceProxy = serviceProxy;
        this.serviceResolver = serviceResolver;
        this.serviceProviderSelector = serviceProviderSelector;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(method.getDeclaringClass() == Object.class) {
            String result1 = method.getName();
            byte invocationId1 = -1;
            switch(result1.hashCode()) {
            case -1776922004:
                if(result1.equals("toString")) {
                    invocationId1 = 0;
                }
                break;
            case -1295482945:
                if(result1.equals("equals")) {
                    invocationId1 = 1;
                }
                break;
            case 147696667:
                if(result1.equals("hashCode")) {
                    invocationId1 = 2;
                }
            }

            switch(invocationId1) {
            case 0:
                return this.targetClass.toString();
            case 1:
                return Boolean.valueOf(this.equals(Proxy.getInvocationHandler(args[0])));
            case 2:
                return Integer.valueOf(this.hashCode());
            default:
                throw new UnsupportedOperationException();
            }
        } else {
            Object result = null;
            if(LOGGER.isDebugEnabled()) {
                String invocationId = generateInvocationId();

                try {
                    String t = this.targetClass.getCanonicalName();
                    String methodName = method.getName();
                    String argsJSON = (new GsonBuilder()).create().toJson(args);
                    LOGGER.debug("invoke method: {}.{}, args: {}, invocationId: {}", new Object[]{t, methodName, argsJSON, invocationId});
                    result = this.doInvoke(method, args);
                    if(result instanceof Future) {
                        LOGGER.debug("method response: {}, invokeId: {}", result, invocationId);
                    } else {
                        String resultJSON = (new GsonBuilder()).create().toJson(result);
                        LOGGER.debug("method response: {}, invokeId: {}", resultJSON, invocationId);
                    }
                } catch (Throwable var10) {
                    LOGGER.debug("method error, invocationId: {}", invocationId, var10);
                    throw var10;
                }
            } else {
                result = this.doInvoke(method, args);
            }

            return result;
        }
    }

    private Object doInvoke(Method method, Object[] args) throws Throwable {
        Object target = null;
        Object exception = null;

        Object var7;
        try {
            List e = this.serviceResolver.resolve(this.serviceName);
            ServiceProvider targetEx1 = this.serviceProviderSelector.select(e);
            if(null == targetEx1) {
                throw new AideException("no available provider for service: " + this.serviceName);
            }

            LOGGER.debug("service: {}, providers: {}, selected provider: {}", new Object[]{this.serviceName, e, targetEx1});
            target = this.serviceProxy.proxy(this.convert(targetEx1));
            var7 = method.invoke(target, args);
        } catch (InvocationTargetException var16) {
            exception = var16;

            for(Throwable targetEx = var16.getTargetException(); targetEx != null; targetEx = ((InvocationTargetException)targetEx).getTargetException()) {
                exception = targetEx;
                if(!(targetEx instanceof InvocationTargetException)) {
                    break;
                }
            }

            throw (Throwable)exception;
        } finally {
            if(null != target) {
                try {
                    this.serviceProxy.destroy(this.targetClass, target, (Throwable)exception);
                } catch (Exception var15) {
                    LOGGER.warn("destroy target object fail", var15);
                }
            }

        }

        return var7;
    }

    private static String removeAsyncSuffix(String serviceName) {
        String ret = serviceName;
        String suffix = ".Async";
        if(serviceName.endsWith(suffix)) {
            int idx = serviceName.lastIndexOf(suffix);
            ret = serviceName.substring(0, idx);
        }

        return ret;
    }

    private JavaServiceProvider convert(ServiceProvider provider) {
        if(provider instanceof JavaServiceProvider) {
            return (JavaServiceProvider)provider;
        } else {
            JavaService javaService = new JavaService(this.targetClass, provider.getService().getVersion(), provider.getService().getId());
            JavaServiceProvider result = new JavaServiceProvider(javaService);
            result.setHost(provider.getHost());
            result.setPort(provider.getPort());
            return result;
        }
    }

    private static String generateInvocationId() {
        return System.currentTimeMillis() + "-" + INDEX.getAndIncrement();
    }
}
