package com.aide.rpc.server;

import com.aide.integration.bootstrap.ConfigUtil;
import com.aide.integration.bootstrap.ThriftServerBootStrap;
import com.aide.rpc.service.CalculatorImpl;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.thrift.transport.TTransportException;

import java.io.IOException;

/**
 * Created by make on 2016/12/27.
 */
public class Server {
    public static void main(String[] params) throws TTransportException, ConfigurationException, IOException {
        Configuration configuration =  ConfigUtil.loadWithCmdArgs(params);
        CalculatorImpl impl = new CalculatorImpl();

        new ThriftServerBootStrap(impl, configuration).start();
        System.out.println("服务已开启");
    }
}
