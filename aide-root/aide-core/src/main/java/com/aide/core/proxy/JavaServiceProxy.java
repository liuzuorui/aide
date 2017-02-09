package com.aide.core.proxy;

import com.aide.core.AideException;

public interface JavaServiceProxy {
    Object proxy(JavaServiceProvider javaServiceProvider) throws AideException;

    void destroy(Class<?> targetClass, Object object, Throwable cause) throws AideException;
}