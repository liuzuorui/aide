package com.aide.core.proxy;

import com.aide.core.Service;
import com.aide.core.ServiceProvider;

public class JavaServiceProvider extends ServiceProvider {
    private final JavaService javaService;

    public JavaServiceProvider(Service service) {
        super(service);
        if(service instanceof JavaService) {
            this.javaService = (JavaService)service;
        } else {
            this.javaService = new JavaService(service);
        }
    }

    public JavaService getJavaService() {
        return this.javaService;
    }
}