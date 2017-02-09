

package com.aide.proxy.thrift;

import com.aide.core.AideException;
import com.aide.core.proxy.JavaServiceProvider;
import com.aide.core.proxy.JavaServiceProxy;
import com.facebook.swift.service.ThriftClientManager;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;

public class ThriftServiceProxy implements JavaServiceProxy, Closeable {
    protected final ThriftClientManager clientManager = new ThriftClientManager();

    public ThriftServiceProxy() {
    }

    public void close() throws IOException {
        closeQuietly(this.clientManager);
    }

    public Object proxy(JavaServiceProvider serviceProvider) throws AideException {
        InetSocketAddress socketAddress = new InetSocketAddress(serviceProvider.getHost(), serviceProvider.getPort());
        return this.proxy(serviceProvider.getJavaService().getTargetClass(), socketAddress);
    }

    public void destroy(Class<?> targetClass, Object obj, Throwable cause) throws AideException {
        if(null != obj) {
            if(obj instanceof Closeable) {
                closeQuietly((Closeable)obj);
            }

        }
    }

    protected <T> T proxy(Class<T> clazz, InetSocketAddress address) {
        return (T)(new ThriftServiceBuilder()).clientManager(this.clientManager).type(clazz).address(address).build();
    }

    protected static void closeQuietly(Closeable closeable) {
        if(null != closeable) {
            try {
                closeable.close();
            } catch (IOException var2) {
                ;
            }
        }

    }
}
