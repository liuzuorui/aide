package com.aide.integration.proxy;

import java.lang.reflect.Method;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.aide.proxy.thrift.ThriftServiceValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AideThriftServiceValidator implements ThriftServiceValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(AideThriftServiceValidator.class);
    private static final String PING_METHOD_NAME = "ping";
    private static final String EXPECTED_RESULT = "htw";
    private volatile long methodTimeout = 5000L;

    public AideThriftServiceValidator() {
    }

    public boolean isValid(Class<?> clazz, Object service) {
        Method pingMethod = null;

        try {
            pingMethod = clazz.getMethod("ping", new Class[0]);
        } catch (Throwable var8) {
            LOGGER.warn("service validation skipped");
            return true;
        }

        try {
            Object e = pingMethod.invoke(service, new Object[0]);
            String result = null;
            if(e instanceof Future) {
                Future future = (Future)e;
                result = (String)future.get(this.methodTimeout, TimeUnit.MILLISECONDS);
            } else {
                result = (String)e;
            }

            return "htw".equals(result);
        } catch (Throwable var7) {
            LOGGER.debug("ping failed", var7);
            return false;
        }
    }

    public void setMethodTimeout(long methodTimeout) {
        this.methodTimeout = methodTimeout;
    }

    public long getMethodTimeout() {
        return this.methodTimeout;
    }
}
