package com.aide.proxy.thrift;

import com.google.common.base.Preconditions;
import java.lang.reflect.Method;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleServiceValidator implements ThriftServiceValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleServiceValidator.class);
    private final String methodName;
    private final Object expectedResult;
    private final Object[] args;
    private volatile long methodTimeout;

    public SimpleServiceValidator(String methodName) {
        this(methodName, (Object[])null, (Object)null);
    }

    public SimpleServiceValidator(String methodName, Object expectedResult) {
        this(methodName, (Object[])null, expectedResult);
    }

    public SimpleServiceValidator(String methodName, Object[] args, Object expectedResult) {
        this.methodTimeout = 5000L;
        Preconditions.checkNotNull(methodName);
        this.methodName = methodName;
        this.args = args;
        this.expectedResult = expectedResult;
    }

    public boolean isValid(Class<?> clazz, Object service) {
        if(null != clazz && null != service) {
            Method method = null;

            try {
                method = clazz.getMethod(this.methodName, new Class[0]);
            } catch (Throwable var8) {
                LOGGER.debug("service not valid, method not found. class: {}, method: {}", new Object[]{clazz, method, var8});
                return false;
            }

            try {
                Object e = null;
                Object result = null;
                if(null == this.args) {
                    e = method.invoke(service, new Object[0]);
                } else {
                    e = method.invoke(service, this.args);
                }

                if(e instanceof Future) {
                    Future future = (Future)e;
                    result = future.get(this.methodTimeout, TimeUnit.MILLISECONDS);
                } else {
                    result = e;
                }

                return null == this.expectedResult?result == null:this.expectedResult.equals(result);
            } catch (Throwable var7) {
                LOGGER.debug("service not valid, invoke failed. class: {}, method: {}", new Object[]{clazz, method, var7});
                return false;
            }
        } else {
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
