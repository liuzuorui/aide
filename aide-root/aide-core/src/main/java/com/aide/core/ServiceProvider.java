package com.aide.core;

import java.util.Objects;

/**
 * Created by Administrator on 2016/12/24.
 */
public class ServiceProvider {

    private  final  Service service;
    private String host;
    private int port;

    public ServiceProvider(String serviceName){
        this(new Service(serviceName));
    }
    public ServiceProvider(Class<?> serviceClass){
        this(new Service(serviceClass));
    }
    public ServiceProvider(Service service){
        this.host = "localhost";
        this.port = 0;
        Objects.requireNonNull(service);
        this.service = service;
    }

    public Service getService() {
        return this.service;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String toString() {
        return String.format("{service: %s, host: %s, port: %d}", new Object[]{this.service, this.host, Integer.valueOf(this.port)});
    }
}
