package io.err0;

import org.apache.maven.plugin.logging.Log;

public class Err0Wrapper {
    public Err0Wrapper(final Log log) {
        this.log = log;
    }
    final Log log;

    public void call(final String args[]) {
        StringBuilder commandLine = new StringBuilder("java -jar /tmp/err0agent.jar");
        for (int i = 0; i < args.length; ) {
            commandLine.append(' ').append(args[i++]);
        }
        log.info(commandLine);
        log.info("GET https://api.github.com/repos/Err0-io/err0agent/releases/latest");
    }
}
