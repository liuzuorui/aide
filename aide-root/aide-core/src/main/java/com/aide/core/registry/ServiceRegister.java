package com.aide.core.registry;

import com.aide.core.AideException;
import com.aide.core.ServiceProvider;

public interface ServiceRegister {
    void register(ServiceProvider serviceProvider) throws AideException;

    void deregister(String serviceId) throws AideException;

    void keepAlive(String serviceId) throws AideException;
}