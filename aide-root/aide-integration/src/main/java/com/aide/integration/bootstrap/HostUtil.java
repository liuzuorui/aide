package com.aide.integration.bootstrap;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class HostUtil {
    private static final String DEFAULT_HOST = findDefaultHostAddr();

    public HostUtil() {
    }

    public static String getDefaultHost() {
        return DEFAULT_HOST;
    }

    private static String findDefaultHostAddr() {
        String defaultAddr = null;

        try {
            Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces();

            label50:
            while(true) {
                while(true) {
                    NetworkInterface networkInterface;
                    do {
                        do {
                            if(!networkInterfaces.hasMoreElements()) {
                                break label50;
                            }

                            networkInterface = (NetworkInterface)networkInterfaces.nextElement();
                        } while(networkInterface.isLoopback());
                    } while(!networkInterface.isUp());

                    Enumeration inetAddresses = networkInterface.getInetAddresses();

                    while(inetAddresses.hasMoreElements()) {
                        InetAddress inetAddress = (InetAddress)inetAddresses.nextElement();
                        if(!inetAddress.isLinkLocalAddress()) {
                            String hostAddress = inetAddress.getHostAddress();
                            if(hostAddress != null && hostAddress.split("\\.").length == 4) {
                                defaultAddr = hostAddress;
                                break;
                            }
                        }
                    }
                }
            }
        } catch (Exception var6) {
            ;
        }

        if(null == defaultAddr) {
            defaultAddr = "localhost";
        }

        return defaultAddr;
    }
}
