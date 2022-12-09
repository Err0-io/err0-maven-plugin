package io.err0;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;

public abstract class BaseMojo extends AbstractMojo {

    @Parameter( defaultValue = "${project.basedir}", property = "basedir", required = false )
    protected String basedir;

    @Parameter( property = "token", required = true )
    protected String token;
}
