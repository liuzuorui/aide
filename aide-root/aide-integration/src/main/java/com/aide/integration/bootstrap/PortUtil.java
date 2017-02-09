package com.aide.integration.bootstrap;

import java.io.IOException;
import java.net.ServerSocket;

public class PortUtil {
    public PortUtil() {
    }

    public static int getRandomPort() {
        try {
            ServerSocket e = new ServerSocket(0);
            Throwable var1 = null;

            int var2;
            try {
                var2 = e.getLocalPort();
            } catch (Throwable var12) {
                var1 = var12;
                throw var12;
            } finally {
                if(e != null) {
                    if(var1 != null) {
                        try {
                            e.close();
                        } catch (Throwable var11) {
                            var1.addSuppressed(var11);
                        }
                    } else {
                        e.close();
                    }
                }

            }

            return var2;
        } catch (IOException var14) {
            return -1;
        }
    }
}
