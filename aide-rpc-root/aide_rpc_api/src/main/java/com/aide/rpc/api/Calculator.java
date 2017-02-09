package com.aide.rpc.api;

import com.facebook.swift.codec.*;
import com.facebook.swift.codec.ThriftField.Requiredness;
import com.facebook.swift.service.*;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.*;
import java.util.*;
import com.aide.rpc.common.AideRpcService;

@ThriftService("Calculator")
public interface Calculator extends AideRpcService
{
    @ThriftService("Calculator")
    public interface Async extends AideRpcService.Async{
        @ThriftMethod(value = "add")
        ListenableFuture<Integer> add(
            @ThriftField(value=1, name="num1", requiredness=Requiredness.NONE) final int num1,
            @ThriftField(value=2, name="num2", requiredness=Requiredness.NONE) final int num2
        );
    }
    @ThriftMethod(value = "add")
    int add(
        @ThriftField(value=1, name="num1", requiredness=Requiredness.NONE) final int num1,
        @ThriftField(value=2, name="num2", requiredness=Requiredness.NONE) final int num2
    ) throws org.apache.thrift.TException;
}