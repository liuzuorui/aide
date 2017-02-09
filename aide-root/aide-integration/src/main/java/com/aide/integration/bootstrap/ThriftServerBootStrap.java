package com.aide.integration.bootstrap;

import com.aide.core.Service;
import com.aide.core.ServiceProvider;
import com.aide.integration.registry.RegisterExecutor;
import com.aide.registry.consul.ConsulRegistry;
import com.facebook.swift.codec.ThriftCodec;
import com.facebook.swift.codec.ThriftCodecManager;
import com.facebook.swift.service.ThriftServer;
import com.facebook.swift.service.ThriftServerConfig;
import com.facebook.swift.service.ThriftServiceProcessor;
import io.airlift.units.Duration;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThriftServerBootStrap implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThriftServerBootStrap.class);
    private static final long RETRY_INTERVAL = 5000L;
    private final ThriftServer thriftServer;
    private boolean skipRegistration;
    private ConsulRegistry registry;
    private ServiceProvider serviceProvider;
    private long keepAliveInterval;
    private RegisterExecutor registerExecutor;
    private volatile boolean started;

    public ThriftServerBootStrap(Object targetObject, Configuration conf) {
        this((new ThriftServerBootStrapConfig(targetObject)).config(conf));
    }

    private ThriftServerBootStrap(ThriftServerBootStrapConfig config) {
        this.keepAliveInterval = 5000L;
        this.thriftServer = makeThriftServer(config);
        this.skipRegistration = config.isSkipRegistration();
        if(!this.skipRegistration) {
            if(config.getTargetClass() == null) {
                Object targetObject = config.getTargetObject();
                config.setTargetClass(getTargetInterface(targetObject));
            }

            this.registry = makeConsulRegistry(config);
            this.serviceProvider = makeServiceProvider(config);
            this.keepAliveInterval = config.getKeepAliveInterval();
        }

        this.addShutdownHook();
    }

    public synchronized void close() throws IOException {
        if(this.started) {
            if(!this.skipRegistration) {
                this.destroyRegisterExecutor();
            }

            this.thriftServer.close();
            this.started = false;
        }
    }

    public synchronized void start() {
        if(!this.started) {
            this.thriftServer.start();
            if(!this.skipRegistration) {
                this.launchRegisterExecutor();
            }

            this.started = true;
        }
    }

    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                this.close();
            } catch (IOException var2) {
                ;
            }

        }));
    }

    private void launchRegisterExecutor() {
        this.destroyRegisterExecutor();
        this.registerExecutor = new RegisterExecutor(this.registry, this.serviceProvider, this.keepAliveInterval);
        this.registerExecutor.start();
    }

    private void destroyRegisterExecutor() {
        if(null != this.registerExecutor) {
            this.registerExecutor.interrupt();
            this.registerExecutor = null;
        }

    }

    private static Class<?> getTargetInterface(Object targetObject) {
        Class[] interfaces = targetObject.getClass().getInterfaces();
        if(null != interfaces && interfaces.length != 0) {
            if(interfaces.length > 1) {
                throw new IllegalArgumentException("more than one direct interface: " + targetObject.getClass());
            } else {
                return interfaces[0];
            }
        } else {
            throw new IllegalArgumentException("no direct interface found: " + targetObject.getClass());
        }
    }

    private static String generateServiceId(Class<?> clazz, String host, int port, String version) {
        return clazz.getCanonicalName() + "@" + host + ":" + port + "V" + version;
    }

    private static ServiceProvider makeServiceProvider(ThriftServerBootStrapConfig config) {
        Class targetClass = config.getTargetClass();
        String serverHost = config.getServerHost();
        int serverPort = config.getServerPort();
        String serviceVersion = config.getServiceVersion();
        String serviceId = generateServiceId(targetClass, serverHost, serverPort, serviceVersion);
        Service service = new Service(targetClass, serviceVersion, serviceId);
        ServiceProvider provider = new ServiceProvider(service);
        provider.setHost(serverHost);
        provider.setPort(serverPort);
        return provider;
    }

    private static ConsulRegistry makeConsulRegistry(ThriftServerBootStrapConfig config) {
        return new ConsulRegistry(config.getConsulHost(), config.getConsulPort(), config.getServiceTTL());
    }

    private static ThriftServer makeThriftServer(ThriftServerBootStrapConfig config) {
        Object targetObject = config.getTargetObject();
        String serverHost = config.getServerHost();
        int serverPort = config.getServerPort();
        ThriftServiceProcessor processor = new ThriftServiceProcessor(new ThriftCodecManager(new ThriftCodec[0]), new ArrayList(), new Object[]{targetObject});
        ThriftServerConfig serverConfig = new ThriftServerConfig();
        serverConfig.setBindAddress(serverHost);
        serverConfig.setPort(serverPort);
        serverConfig.setIdleConnectionTimeout(new Duration(365.0D, TimeUnit.DAYS));
        return new ThriftServer(processor, serverConfig);
    }
}
