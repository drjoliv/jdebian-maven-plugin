package drjoliv.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import drjoliv.jfunc.contorl.either.Either;
import drjoliv.jfunc.contorl.trys.Try;

@Mojo( name = "install", requiresOnline = false, requiresProject = true
    , requiresDependencyResolution = ResolutionScope.RUNTIME
    , threadSafe = true, defaultPhase = LifecyclePhase.INSTALL )
public class Install extends AbstractDebianMojo {

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {

    Either<Exception, Integer> e = 
      Try.with(() -> 
        Runtime.getRuntime()
          .exec("sudo dpkg -i " + artifactName(), null, targetDir()))
      .bind(process -> {
        return Try.with( () -> {
          BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
          br.lines()
            .forEach(line -> info(line));
          br.close();
          return process.waitFor();
        });
      })
      .recoverWith(IOException.class, ex -> {
        return Try.failure(ex); 
      })
      .recoverWith(InterruptedException.class, ex -> {
        return Try.failure(ex); 
      })
      .run();
  }
}
