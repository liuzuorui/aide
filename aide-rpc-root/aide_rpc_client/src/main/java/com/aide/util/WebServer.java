package com.aide.util;

import com.aide.integration.bootstrap.ConfigUtil;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationConverter;
import org.springframework.context.ApplicationContext;

import java.util.Properties;

/**
 * Created by Administrator on 2016/11/9.
 */
public class WebServer extends ContextSupport{

    public static void main(String[] argv) throws Exception {
        Configuration configuration = ConfigUtil.loadWithCmdArgs(argv);
        String serviceIgnoredLogging = configuration.getString("log.service.ignored", "web.common.file.get,web.common.file.upload");

        Properties properties = ConfigurationConverter.getProperties(configuration);
        ApplicationContext context = createContext(properties);
//        WebServiceImpl impl = new WebServiceImpl();
//        impl.setApplicationContext(context);
//        impl.setServicesIgnoredLogging(serviceIgnoredLogging);
//
//        new ThriftServerBootStrap(impl, configuration).start();
    }


}
