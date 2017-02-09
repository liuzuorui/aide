package com.aide.integration.registry;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.aide.core.registry.ServiceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ResolveExecutor extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResolveExecutor.class);
    private final ServiceResolver resolver;
    private final long interval;
    private final ResolveListener listener;
    private final Object guard = new Object();
    private volatile LinkedList<String> serviceNames = new LinkedList();

    ResolveExecutor(ServiceResolver resolver, long interval, ResolveListener listener) {
        Objects.requireNonNull(resolver);
        Objects.requireNonNull(listener);
        String name = String.format("%s", new Object[]{this.getClass().getSimpleName()});
        this.setName(name);
        this.setDaemon(true);
        this.resolver = resolver;
        this.interval = interval;
        this.listener = listener;
    }

    void addServiceName(String serviceName) {
        Objects.requireNonNull(serviceName);
        synchronized(this) {
            this.serviceNames.remove(serviceName);
            this.serviceNames.addFirst(serviceName);
        }
    }

    public void run() {
        LOGGER.info("ResolveExecutor start");

        while(true) {
            LinkedList currentServiceNames;
            synchronized(this) {
                currentServiceNames = new LinkedList(this.serviceNames);
            }

            Iterator var2 = currentServiceNames.iterator();

            while(var2.hasNext()) {
                String e = (String)var2.next();

                try {
                    List e1 = this.resolver.resolve(e);
                    this.listener.didResolveDone(e, e1);
                } catch (Exception var6) {
                    this.logException(e, var6);
                }
            }

            Object var10 = this.guard;
            synchronized(var10) {
                try {
                    var10.wait(this.interval);
                    continue;
                } catch (InterruptedException var8) {
                    ;
                }
            }

            LOGGER.info("ResolveExecutor exit");
            return;
        }
    }

    private void logException(String serviceName, Exception e) {
        LOGGER.warn("resolve service provider fail, serviceName: " + serviceName, e);
    }
}
