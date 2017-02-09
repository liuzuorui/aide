package com.aide.proxy.thrift.poolable;

import com.aide.core.AideException;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import com.aide.proxy.thrift.ThriftServiceProxy;
import com.aide.proxy.thrift.ThriftServiceValidator;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.apache.thrift.transport.TTransportException;

public class PoolableThriftServiceProxy extends ThriftServiceProxy implements Closeable {
    private volatile ConcurrentHashMap<Object, InetSocketAddress> serviceAddressMap;
    private GenericKeyedObjectPool<Pair<Class<?>, InetSocketAddress>, Object> servicePool;

    public PoolableThriftServiceProxy() {
        this((GenericKeyedObjectPoolConfig)null, (ThriftServiceValidator)null);
    }

    public PoolableThriftServiceProxy(GenericKeyedObjectPoolConfig poolConfig) {
        this(poolConfig, (ThriftServiceValidator)null);
    }

    public PoolableThriftServiceProxy(GenericKeyedObjectPoolConfig poolConfig, ThriftServiceValidator serviceValidator) {
        this.serviceAddressMap = new ConcurrentHashMap();
        if(null == poolConfig) {
            poolConfig = new GenericKeyedObjectPoolConfig();
        }

        ThriftServiceObjectFactory serviceObjectFactory = new ThriftServiceObjectFactory(this.clientManager, serviceValidator);
        this.servicePool = new GenericKeyedObjectPool(serviceObjectFactory, poolConfig);
    }

    public void close() throws IOException {
        this.servicePool.close();
        super.close();
    }

    public <T> T proxy(Class<T> clazz, InetSocketAddress address) {
        try {
            Object e = this.servicePool.borrowObject(Pair.of(clazz, address));
            this.serviceAddressMap.put(e, address);
            return (T)e;
        } catch (Exception var5) {
            String errMsg = "could not get service object for class: " + clazz.getCanonicalName() + " from pool with address: " + address;
            throw new AideException(errMsg, var5);
        }
    }

    public void destroy(Class<?> targetClass, Object obj, Throwable cause) throws AideException {
        if(null != obj) {
            InetSocketAddress address = (InetSocketAddress)this.serviceAddressMap.get(obj);
            if(null != cause && cause instanceof TTransportException) {
                try {
                    this.servicePool.invalidateObject(Pair.of(targetClass, address), obj);
                } catch (Exception var6) {
                    throw new AideException(var6);
                }
            } else {
                this.servicePool.returnObject(Pair.of(targetClass, address), obj);
            }

            this.serviceAddressMap.remove(obj);
        }
    }
}
