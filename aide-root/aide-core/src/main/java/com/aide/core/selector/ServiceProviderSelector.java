package com.aide.core.selector;

import com.aide.core.ServiceProvider;

import java.util.List;

public interface ServiceProviderSelector {
    ServiceProvider select(List<ServiceProvider> providers);
}