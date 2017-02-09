package com.aide.util;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.Properties;

/**
 * Created by panleiming on 16-3-15.
 */
public abstract class ContextSupport {
    public static ApplicationContext createContext(BeanFactoryPostProcessor postProcessor) {
        ClassPathXmlApplicationContext parentContent = new ClassPathXmlApplicationContext(new String[] { "/app.xml" },
                false);
        parentContent.setValidating(false);
        parentContent.addBeanFactoryPostProcessor(postProcessor);
        parentContent.refresh();
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.setParent(parentContent);
        context.scan("com.htw.test.web");
        context.refresh();
        return context;
    }

    public static ApplicationContext createContext(Properties properties) {
        PropertyPlaceholderConfigurer props = new PropertyPlaceholderConfigurer();
        props.setProperties(properties);
        return createContext(props);
    }

    public static ApplicationContext createContext(Resource propResource) {
        PropertyPlaceholderConfigurer props = new PropertyPlaceholderConfigurer();
        props.setLocation(propResource);
        return createContext(props);
    }

    public static ApplicationContext createContext(){
        Resource propResource = new ClassPathResource("server.conf");
        return createContext(propResource);
    }
}
