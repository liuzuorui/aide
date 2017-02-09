package com.aide.proxy.thrift;

import com.aide.core.AideException;
import com.facebook.nifty.client.FramedClientChannel;
import com.facebook.nifty.client.FramedClientConnector;
import com.facebook.nifty.client.NiftyClientChannel;
import com.facebook.swift.service.ThriftClientManager;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListenableFuture;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class ThriftServiceBuilder {
    private ThriftClientManager clientManager;
    private Class<?> type;
    private InetSocketAddress address;
    private long connectTimeoutMills = -1L;

    public ThriftServiceBuilder() {
    }

    public ThriftClientManager clientManager() {
        return this.clientManager;
    }

    public ThriftServiceBuilder clientManager(ThriftClientManager clientManager) {
        this.clientManager = clientManager;
        return this;
    }

    public Class<?> type() {
        return this.type;
    }

    public ThriftServiceBuilder type(Class<?> type) {
        this.type = type;
        return this;
    }

    public InetSocketAddress address() {
        return this.address;
    }

    public ThriftServiceBuilder address(InetSocketAddress address) {
        this.address = address;
        return this;
    }

    public long connectTimeoutMills() {
        return this.connectTimeoutMills;
    }

    public ThriftServiceBuilder connectTimeoutMills(long connectTimeoutMills) {
        this.connectTimeoutMills = connectTimeoutMills;
        return this;
    }

    public Object build() {
        Preconditions.checkNotNull(this.type);
        Preconditions.checkNotNull(this.address);
        Preconditions.checkNotNull(this.clientManager);
        return this.createService();
    }

    private Object createService() {
        NiftyClientChannel channel = this.connect();
        Object service = this.clientManager.createClient(channel, this.type);
        return service;
    }

    private NiftyClientChannel connect() {
        try {
            FramedClientConnector e = new FramedClientConnector(this.address);
            ListenableFuture errMsg1 = this.clientManager.createChannel(e);
            FramedClientChannel framedClientChannel = null;
            if(this.connectTimeoutMills <= 0L) {
                framedClientChannel = (FramedClientChannel)errMsg1.get();
            } else {
                framedClientChannel = (FramedClientChannel)errMsg1.get(this.connectTimeoutMills, TimeUnit.MILLISECONDS);
            }

            return framedClientChannel;
        } catch (Exception var4) {
            String errMsg = "connect to address failed: " + this.address;
            throw new AideException(errMsg, var4);
        }
    }
}
