//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.facebook.mojo.alternative;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.scm.log.ScmLogger;

public class ScmSystemStreamLog extends SystemStreamLog implements ScmLogger {
    public ScmSystemStreamLog() {
    }

    public void debug(String s) {
        super.debug(s);
    }

    public void debug(String s, Throwable throwable) {
        super.debug(s, throwable);
    }

    public void info(String s) {
        super.info(s);
    }

    public void info(String s, Throwable throwable) {
        super.info(s, throwable);
    }

    public void warn(String s) {
        super.warn(s);
    }

    public void warn(String s, Throwable throwable) {
        super.warn(s, throwable);
    }

    public void error(String s) {
        super.error(s);
    }

    public void error(String s, Throwable throwable) {
        super.error(s, throwable);
    }
}
