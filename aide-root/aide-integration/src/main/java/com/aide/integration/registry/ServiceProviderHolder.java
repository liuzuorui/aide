package com.aide.integration.registry;

import com.aide.core.AideException;
import com.aide.core.ServiceProvider;
import com.aide.core.registry.ServiceResolver;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceProviderHolder implements ServiceResolver, ResolveListener {
    private final ResolveExecutor executor;
    private final ServiceResolver resolver;
    private static final long UPDATE_INTERVAL = 2000L;
    private volatile ConcurrentHashMap<String, List<ServiceProvider>> providerNameMap = new ConcurrentHashMap();

    public List<ServiceProvider> resolve(String serviceName) throws AideException {
        if(null == serviceName) {
            return new LinkedList();
        } else {
            Object result = (List)this.providerNameMap.get(serviceName);
            if(null == result || ((List)result).isEmpty()) {
                result = this.resolver.resolve(serviceName);
                if(null != result && !((List)result).isEmpty()) {
                    this.providerNameMap.put(serviceName, (List)result);
                }

                this.executor.addServiceName(serviceName);
            }

            if(null == result) {
                result = new LinkedList();
            }

            return (List)result;
        }
    }

    public ServiceProviderHolder(ServiceResolver resolver) {
        this.resolver = resolver;
        this.executor = new ResolveExecutor(resolver, 2000L, this);
        this.executor.start();
    }

    public void didResolveDone(String serviceName, List<ServiceProvider> providers) {
        if(null != serviceName && null != providers) {
            this.providerNameMap.put(serviceName, providers);
        }
    }
}
