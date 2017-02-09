package com.aide.core.proxy;

import com.aide.core.Service;

import java.util.UUID;

/**
 * Created by Administrator on 2016/12/24.
 */
public class JavaService extends Service {
    private final Class<?> targetClass;

    public JavaService(Class<?> targetClass) {
        this(targetClass, "1.0.0");
    }

    public JavaService(Class<?> targetClass, String version) {
        this(targetClass, version, UUID.randomUUID().toString());
    }

    public JavaService(Class<?> targetClass, String version, String id) {
        super(targetClass.getCanonicalName(), version, id);
        this.targetClass = targetClass;
    }

    public JavaService(Service service) {
        super(service.getName(), service.getVersion(), service.getId());
        if (service instanceof Service) {
            this.targetClass = ((JavaService) service).getClass();
        } else {
            this.targetClass = ClassUtil.toClass(service.getName());
        }
    }

    public Class<?> getTargetClass() {
        return this.targetClass;
    }
}
