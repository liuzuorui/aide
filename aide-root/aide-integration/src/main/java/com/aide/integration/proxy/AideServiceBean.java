package com.aide.integration.proxy;

import com.aide.core.ServiceProvider;
import com.aide.core.proxy.JavaService;
import com.aide.core.proxy.JavaServiceProxy;
import com.aide.core.registry.ServiceResolver;
import com.aide.core.registry.jvm.JVMRegistry;
import com.aide.core.selector.ServiceProviderRandomSelector;
import com.aide.core.selector.ServiceProviderSelector;
import org.springframework.beans.factory.FactoryBean;

import java.io.Closeable;
import java.lang.reflect.Proxy;
import java.util.Collections;

/**
 * Created by Administrator on 2016/12/24.
 */
public class AideServiceBean implements FactoryBean {
    private final JavaService javaService;
    private final JavaServiceProxy serviceProxy;
    private final ServiceResolver serviceResolver;
    private final ServiceProviderSelector serviceProviderSelector;

    public AideServiceBean(Class<?> targetClass, JavaServiceProxy serviceProxy, ServiceProvider serviceProvider) {
        this(new JavaService(targetClass), serviceProxy, serviceProvider);
    }

    public AideServiceBean(Class<?> targetClass, JavaServiceProxy serviceProxy, ServiceResolver serviceResolver) {
        this(new JavaService(targetClass), serviceProxy, serviceResolver);
    }

    public AideServiceBean(JavaService javaService, JavaServiceProxy serviceProxy, ServiceProvider serviceProvider) {
        this((JavaService)javaService, serviceProxy, (ServiceResolver)(new JVMRegistry(Collections.singletonList(serviceProvider))));
    }

    public AideServiceBean(JavaService javaService, JavaServiceProxy serviceProxy, ServiceResolver serviceResolver) {
        this(javaService, serviceProxy, serviceResolver, (ServiceProviderSelector)null);
    }

    public AideServiceBean(JavaService javaService, JavaServiceProxy serviceProxy, ServiceResolver serviceResolver, ServiceProviderSelector serviceProviderSelector) {
        if(null == serviceProviderSelector) {
            serviceProviderSelector = ServiceProviderRandomSelector.get();
        }

        this.javaService = javaService;
        this.serviceProxy = serviceProxy;
        this.serviceResolver = serviceResolver;
        this.serviceProviderSelector = serviceProviderSelector;
    }

    public Object getObject() throws Exception {
        Class targetClass = this.javaService.getTargetClass();
        String serviceName = this.javaService.getName();
        AideServiceInvocationHandler handler = new AideServiceInvocationHandler(targetClass, serviceName, this.serviceProxy, this.serviceResolver, this.serviceProviderSelector);
        return targetClass.cast(Proxy.newProxyInstance(targetClass.getClassLoader(), new Class[]{targetClass, Closeable.class}, handler));
    }

    public Class<?> getObjectType() {
        return this.javaService.getTargetClass();
    }

    public boolean isSingleton() {
        return true;
    }
}

