package com.aide.rpc.common;

import com.facebook.swift.codec.*;
import com.facebook.swift.codec.ThriftField.Requiredness;
import com.facebook.swift.service.*;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import org.apache.thrift.TException;

import java.io.*;
import java.util.*;

@ThriftService("AideRpcService")
public interface AideRpcService
{
    @ThriftMethod("ping")
    default String ping() throws TException {
        return "htw";
    }

    @ThriftMethod("noop")
    default void noop() throws TException {
    }

    @ThriftService("com.aide.rpc.common.AideRpcService")
    public interface Async {
        @ThriftMethod("ping")
        default ListenableFuture<String> ping() throws TException {
            return ListenableFutureTask.create(() -> {
                return "htw";
            });
        }

        @ThriftMethod("noop")
        default ListenableFuture<Void> noop() throws TException {
            return ListenableFutureTask.create(() -> {
                return null;
            });
        }
    }
}