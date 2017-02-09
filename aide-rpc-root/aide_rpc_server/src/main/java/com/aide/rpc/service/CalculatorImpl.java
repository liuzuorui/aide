package com.aide.rpc.service;

import com.aide.rpc.api.Calculator;
import com.facebook.swift.codec.ThriftField;
import org.apache.thrift.TException;

/**
 * Created by make on 2016/12/27.
 */
public class CalculatorImpl implements Calculator {

    public int add(@ThriftField(value = 1, name = "num1", requiredness = ThriftField.Requiredness.NONE) int num1, @ThriftField(value = 2, name = "num2", requiredness = ThriftField.Requiredness.NONE) int num2) throws TException {

        return  num1 + num2;
    }

    public void zip() throws TException {
        System.out.println("zip suscss");
    }
}
