package com.aide.integration.registry;

import com.aide.core.ServiceNotFoundException;
import com.aide.core.ServiceProvider;
import com.aide.core.registry.ServiceRegister;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterExecutor extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterExecutor.class);
    private final ServiceRegister register;
    private final ServiceProvider serviceProvider;
    private final long interval;
    private final Object guard = new Object();
    private boolean registered = false;

    public RegisterExecutor(ServiceRegister register, ServiceProvider serviceProvider, long interval) {
        String name = this.getClass().getSimpleName();
        this.setName(name);
        this.setDaemon(true);
        Preconditions.checkNotNull(register);
        Preconditions.checkNotNull(serviceProvider);
        this.register = register;
        this.serviceProvider = serviceProvider;
        this.interval = interval;
    }

    public void run() {
        while(true) {
            if(!this.registered) {
                this.registerProvider();
            }

            if(this.registered) {
                this.keepAlive();
            }

            Object var1 = this.guard;
            synchronized(this.guard) {
                try {
                    this.guard.wait(this.interval);
                    continue;
                } catch (InterruptedException var4) {
                    ;
                }
            }

            this.deregisterProvider();
            return;
        }
    }

    private void keepAlive() {
        String serviceId = null;

        try {
            serviceId = this.serviceProvider.getService().getId();
            this.register.keepAlive(serviceId);
            LOGGER.debug("keep alive success, serviceId: {}", serviceId);
        } catch (Exception var3) {
            LOGGER.warn("keep alive fail, serviceId: {}", serviceId, var3);
            if(var3 instanceof ServiceNotFoundException) {
                this.registered = false;
            }
        }

    }

    private void registerProvider() {
        try {
            this.register.register(this.serviceProvider);
            this.registered = true;
            LOGGER.info("register service success. provider: {}", this.serviceProvider);
        } catch (Exception var2) {
            LOGGER.warn("register service failed. provider: {}", this.serviceProvider, var2);
        }

    }

    private void deregisterProvider() {
        try {
            String e = this.serviceProvider.getService().getId();
            this.register.deregister(e);
            this.registered = false;
            LOGGER.info("deregister service success. provider: {}", this.serviceProvider);
        } catch (Exception var2) {
            LOGGER.warn("deregister service failed. provider: {}", this.serviceProvider, var2);
        }

    }
}
