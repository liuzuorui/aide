package com.aide.core.proxy;

import com.aide.core.AideException;

public class ClassUtil {
    public ClassUtil() {
    }

    public static Class<?> toClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException var2) {
            throw new AideException(var2);
        }
    }
}