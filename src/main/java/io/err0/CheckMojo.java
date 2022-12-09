package io.err0;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo( name = "check", defaultPhase = LifecyclePhase.NONE )
public class CheckMojo
    extends BaseMojo
{
    public void execute()
        throws MojoExecutionException
    {
        Err0Wrapper err0Wrapper = new Err0Wrapper(this);
        err0Wrapper.call(new String[] { "--token", token, "--analyse", "--check", baseDir});
    }
}
