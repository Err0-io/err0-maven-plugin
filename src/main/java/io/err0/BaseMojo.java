package io.err0;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

public abstract class BaseMojo extends AbstractMojo {

    @Parameter( defaultValue = ".", property = "baseDir" )
    protected String baseDir;

    @Parameter( property = "token", required = true )
    protected String token;

    @Parameter( defaultValue = "${project.build.directory}", property = "agentDir" )
    private String agentDirectory;

    protected String getErr0AgentJar() {
        return agentDirectory + File.separator + "err0agent-java_1_8-fat.jar";
    }
}
