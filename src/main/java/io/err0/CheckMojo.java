package io.err0;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo( name = "check", defaultPhase = LifecyclePhase.NONE )
public class CheckMojo
    extends BaseMojo
{
    public void execute()
        throws MojoExecutionException
    {
        final Log log = getLog();
        Err0Wrapper err0Wrapper = new Err0Wrapper(log);
        err0Wrapper.call(new String[] { "--token", token, "--analyse", "--check", basedir });
    }
}
