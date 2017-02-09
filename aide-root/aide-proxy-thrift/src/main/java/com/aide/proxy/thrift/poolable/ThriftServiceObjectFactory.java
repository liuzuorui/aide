package com.aide.proxy.thrift.poolable;

import com.aide.proxy.thrift.ThriftServiceBuilder;
import com.aide.proxy.thrift.ThriftServiceValidator;
import com.facebook.swift.service.ThriftClientManager;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;

class ThriftServiceObjectFactory extends BaseKeyedPooledObjectFactory<Pair<Class<?>, InetSocketAddress>, Object> {
    private final ThriftClientManager clientManager;
    private final ThriftServiceValidator serviceValidator;

    public ThriftServiceObjectFactory(ThriftClientManager clientManager) {
        this(clientManager, (ThriftServiceValidator)null);
    }

    public ThriftServiceObjectFactory(ThriftClientManager clientManager, ThriftServiceValidator serviceValidator) {
        this.clientManager = clientManager;
        this.serviceValidator = serviceValidator;
    }

    public Object create(Pair<Class<?>, InetSocketAddress> pair) throws Exception {
        return (new ThriftServiceBuilder()).clientManager(this.clientManager).type((Class)pair.getKey()).address((InetSocketAddress)pair.getValue()).build();
    }

    public PooledObject<Object> wrap(Object value) {
        return new DefaultPooledObject(value);
    }

    public void destroyObject(Pair<Class<?>, InetSocketAddress> key, PooledObject<Object> p) throws Exception {
        Object service = p.getObject();
        if(service instanceof Closeable) {
            closeQuietly((Closeable)service);
        }

    }

    public boolean validateObject(Pair<Class<?>, InetSocketAddress> key, PooledObject<Object> p) {
        if(null == this.serviceValidator) {
            return true;
        } else {
            Object service = p.getObject();
            return this.serviceValidator.isValid((Class)key.getKey(), service);
        }
    }

    private static void closeQuietly(Closeable closeable) {
        if(null != closeable) {
            try {
                closeable.close();
            } catch (IOException var2) {
                ;
            }
        }

    }
}
