package com.aide.core;

import java.util.Objects;
import java.util.UUID;

/**
 * Created by Administrator on 2016/12/24.
 */
public class Service {
    public  static final String DEFAULT_VERSION ="1.0.0";
    private final String id ;
    private final String name;
    private final String version;

    public Service(Class<?> targetClass) {
        this(targetClass.getCanonicalName());
    }

    public Service(Class<?> targetClass, String version) {
        this(targetClass.getCanonicalName(), version);
    }

    public Service(Class<?> targetClass, String version, String id) {
        this(targetClass.getCanonicalName(), version, id);
    }

    public Service(String name) {
        this((String)name, (String)null, (String)null);
    }

    public Service(String name, String version) {
        this((String)name, version, (String)null);
    }

    public Service(String name, String version, String id) {
        name = trimToNull(name);
        version = trimToNull(version);
        id = trimToNull(id);
        Objects.requireNonNull(name, "service name should not be blank");
        if(null == version) {
            version = "1.0.0";
        }

        if(null == id) {
            id = UUID.randomUUID().toString();
        }

        this.name = name.trim();
        this.version = version.trim();
        this.id = id.trim();
    }

    private static String trimToNull(String str) {
        if(null == str) {
            return null;
        } else {
            str = str.trim();
            return str.isEmpty()?null:str;
        }
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public String toString() {
        return String.format("{id: %s, name: %s, version: %s}", new Object[]{this.id, this.name, this.version});
    }
}
