package com.aide.integration.bootstrap;

import com.aide.core.AideException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.plist.PropertyListConfiguration;

import org.apache.commons.configuration.Configuration;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

public class ConfigUtil {
    private static final String SERVER_CONF_FILE_NAME = "server.conf";

    public ConfigUtil() {
    }

    public static Configuration loadWithCmdArgs(String[] args) {
        String configFilePath = null;
        if(null != args && args.length != 0) {
            configFilePath = args[0];
        } else {
            configFilePath = "server.conf";
        }

        try {
            return load(configFilePath);
        } catch (Exception var3) {
            throw new AideException(var3);
        }
    }

    public static Configuration load(String configFilePath) throws Exception {
        URL confFileURL = null;
        File configFile = new File(configFilePath);
        if(configFile.exists()) {
            confFileURL = configFile.toURI().toURL();
        } else {
            confFileURL = ClassLoader.getSystemResource(configFilePath);
        }

        if(null == confFileURL) {
            throw new FileNotFoundException(configFilePath);
        } else {
            return load(confFileURL);
        }
    }

    public static Configuration load(URL configFileURL) {
        Object config = null;

        try {
            String e = configFileURL.toURI().normalize().toString();
            if(e.endsWith(".xml")) {
                config = new XMLConfiguration(configFileURL);
            } else if(e.endsWith(".plist")) {
                config = new PropertyListConfiguration(configFileURL);
            } else {
                config = new PropertiesConfiguration(configFileURL);
            }

            return (Configuration)config;
        } catch (Exception var3) {
            throw new IllegalArgumentException("illegal configuration file", var3);
        }
    }
}
