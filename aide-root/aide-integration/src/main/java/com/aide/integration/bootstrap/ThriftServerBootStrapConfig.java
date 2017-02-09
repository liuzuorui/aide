package com.aide.integration.bootstrap;

import com.aide.core.proxy.ClassUtil;
import org.apache.commons.configuration.Configuration;

class ThriftServerBootStrapConfig {
    private static final String HOST_KEY = "host";
    private static final String PORT_KEY = "port";
    private static final String CONSUL_HOST_KEY = "consul.host";
    private static final String CONSUL_PORT_KEY = "consul.port";
    private static final String CONSUL_CHECK_INTERVAL_KEY = "consul.check.interval";
    private static final String CONSUL_SERVICE_TTL_KEY = "consul.service.ttl";
    private static final String SERVICE_CLASS_KEY = "serviceClass";
    private static final String SERVICE_VERSION_KEY = "serviceVersion";
    private static final String SKIP_REGISTRATION_KEY = "skipRegistration";
    private Object targetObject;
    private Class<?> targetClass;
    private String serverHost;
    private int serverPort;
    private boolean skipRegistration;
    private String serviceVersion = "1.0.0";
    private long keepAliveInterval = 5000L;
    private long serviceTTL = 16000L;
    private String consulHost = "localhost";
    private int consulPort = 8500;

    ThriftServerBootStrapConfig() {
    }

    ThriftServerBootStrapConfig(Object targetObject) {
        this.targetObject = targetObject;
    }

    ThriftServerBootStrapConfig config(Configuration config) {
        this.serverHost = config.getString("host", HostUtil.getDefaultHost());
        this.serverPort = config.getInt("port", PortUtil.getRandomPort());
        this.skipRegistration = config.getBoolean("skipRegistration", false);
        if(config.containsKey("serviceVersion")) {
            this.serviceVersion = config.getString("serviceVersion");
        }

        if(config.containsKey("consul.check.interval")) {
            this.keepAliveInterval = config.getLong("consul.check.interval");
        }

        if(config.containsKey("consul.service.ttl")) {
            this.serviceTTL = config.getLong("consul.service.ttl");
        }

        if(config.containsKey("consul.host")) {
            this.consulHost = config.getString("consul.host");
        }

        if(config.containsKey("consul.port")) {
            this.consulPort = config.getInt("consul.port");
        }

        if(config.containsKey("serviceClass")) {
            this.targetClass = ClassUtil.toClass(config.getString("serviceClass"));
        }

        return this;
    }

    public Object getTargetObject() {
        return this.targetObject;
    }

    public void setTargetObject(Object targetObject) {
        this.targetObject = targetObject;
    }

    public Class<?> getTargetClass() {
        return this.targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    public String getServerHost() {
        return this.serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getServiceVersion() {
        return this.serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public long getKeepAliveInterval() {
        return this.keepAliveInterval;
    }

    public void setKeepAliveInterval(long keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }

    public long getServiceTTL() {
        return this.serviceTTL;
    }

    public void setServiceTTL(long serviceTTL) {
        this.serviceTTL = serviceTTL;
    }

    public boolean isSkipRegistration() {
        return this.skipRegistration;
    }

    public void setSkipRegistration(boolean skipRegistration) {
        this.skipRegistration = skipRegistration;
    }

    public String getConsulHost() {
        return this.consulHost;
    }

    public void setConsulHost(String consulHost) {
        this.consulHost = consulHost;
    }

    public int getConsulPort() {
        return this.consulPort;
    }

    public void setConsulPort(int consulPort) {
        this.consulPort = consulPort;
    }
}
