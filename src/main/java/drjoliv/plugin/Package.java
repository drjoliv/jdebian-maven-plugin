package drjoliv.plugin;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.Manifest;
import org.codehaus.plexus.util.FileUtils;

import drjoliv.jfunc.contorl.either.Either;
import drjoliv.jfunc.contorl.trys.Try;
import static drjoliv.jfunc.contorl.trys.Try.*;
import drjoliv.jfunc.data.Unit;


/**
 * Goal which creates a .deb file.
 */
@Mojo( name = "package", requiresOnline = false, requiresProject = true
    , requiresDependencyResolution = ResolutionScope.RUNTIME
    , threadSafe = true, defaultPhase = LifecyclePhase.PACKAGE )
public class Package extends AbstractDebianMojo {

    public void execute() throws MojoExecutionException {
      info("debian-maven-plugin");

        Either<Exception,Unit> e =
        copyDebian()
        .semi(copyDependencies())
        .semi(createJar())
        .semi(createExecutable())
        .semi(createControlFile())
        .semi(packageDeb())
        .semi(cleanUp())
        .run();
    }

    private Try<Unit> copyDebian() {
      info("copying debian");
        return Try.with(() -> {
        if (getSrcDebianFolder().isDirectory())
          FileUtils.copyDirectory(getSrcDebianFolder(), tempDir());
        return Unit.unit; 
        }).recoverWith(IOException.class, e -> {
        error(e.getMessage());
        return failure(e);
      });
    }

    private Try<Unit> createJar() {
        return Try.with(() -> {
          info("create jar");
          JarArchiver jar = new JarArchiver(); 
          jar.addDirectory(getClassFolder());
          Manifest man = new Manifest();
          Manifest.Attribute mainClass = new Manifest.Attribute("Main-Class", mainClass());
          man.addConfiguredAttribute(mainClass);
          jar.addConfiguredManifest(man);
          String jars = getArtifacts()
            .foldr((s,a) -> s + " " + a.getFile().getName(), "")
            .trim();
          Manifest.Attribute classPath = new Manifest.Attribute("Class-Path", jars);
          man.addConfiguredAttribute(classPath);
          jar.setCompress(true);
          jar.setRecompressAddedZips(true);
          jar.setForced(true);
          jar.setDestFile(new File(getPackagedLibsFolder(), getProject().getArtifactId()+"-"+getProject().getVersion()+".jar"));
          jar.createArchive();
          return Unit.unit; 
        }).recoverWith(IOException.class, e -> {
        error(e.getMessage());
        return failure(e);
      });
    }

    private Try<Unit> createExecutable() {
        return Try.with(() -> {
          PrintWriter writer = new PrintWriter(new FileWriter(getExecutable()));
          writer.println("#!/bin/sh");
          writer.println("exec java -jar " + "/usr/share/" + packageName() + "/" + getProject().getArtifactId()+"-"+getProject().getVersion()+".jar \"$@\"");
          writer.close();
          Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-xr-x");
          Files.setPosixFilePermissions(getExecutable().toPath(),perms);
          return Unit.unit; 
        }).recoverWith(IOException.class, e -> {
        error(e.getMessage());
        return failure(e);
      });
    }

    private Try<Unit> copyDependencies() {
        return Try.with(() -> {
          for(Artifact a : getArtifacts()) {
            info(a.getScope());
           FileUtils.copyFileToDirectory(a.getFile(),getPackagedLibsFolder());
          }
          return Unit.unit; 
        }).recoverWith(IOException.class, e -> {
        error(e.getMessage());
        return failure(e);
      });
    }

    private Try<Unit> packageDeb() {
        return Try.with(() -> {
          for( File f : getPackagedLibsFolder().listFiles()) {
              Files.setPosixFilePermissions(f.toPath(), PosixFilePermissions.fromString("rwxr-xr-x"));
          }
          Process p = Runtime.getRuntime()
            .exec("dpkg --build debian " + artifactName() , null, tempDir());
          p.waitFor();
          FileUtils.copyFileToDirectory(new File(tempDir(), artifactName()), targetDir());
          setArtifactFile(artifact());

        return Unit.unit; 
      }).recoverWith(FileSystemException.class, e -> {
        error(e.getMessage());
        return failure(e);
      });
    }

    private Try<Unit> createControlFile() {
        return Try.with(() -> {
          writeCtrlFile(getControlFile());
        return Unit.unit; 
      }).recoverWith(IOException.class, e -> {
        info(e.getMessage());
        return failure(e);
      }).recoverWith(FileNotFoundException.class, e -> {
        error(e.getMessage());
        return failure(e);
      });
    }

    private Try<Unit> cleanUp() {
        return Try.with(() -> {
          FileUtils.deleteDirectory(tempDir());
        return Unit.unit; 
      });
    }

    private void writeCtrlFile(File file) throws IOException {
      ControlFile ctrlFile = new ControlFile();
      ctrlFile.setArchitecture(architecture());
      ctrlFile.setLongDecription(longDescription());
      ctrlFile.setMaintainerEmail(maintainerEmail());
      ctrlFile.setMaintainerName(maintainerName());
      ctrlFile.setPriority(priority());
      ctrlFile.setProgramName(packageName());
      ctrlFile.setSection(section());
      ctrlFile.setShortDecription(shortDescription());
      ctrlFile.setVersionName(version());
      ctrlFile.validate();
      ctrlFile.write(file);
    }
}
