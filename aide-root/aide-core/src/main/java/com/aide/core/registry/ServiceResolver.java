package com.aide.core.registry;

import com.aide.core.AideException;
import com.aide.core.ServiceProvider;

import java.util.List;

public interface ServiceResolver {
    List<ServiceProvider> resolve(String serviceName) throws AideException;
}