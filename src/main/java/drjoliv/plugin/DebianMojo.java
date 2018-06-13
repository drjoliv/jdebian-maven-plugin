package drjoliv.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.List;
import java.util.Set;

import org.apache.maven.Maven;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.JarArchiver.FilesetManifestConfig;
import org.codehaus.plexus.archiver.jar.Manifest;
import org.codehaus.plexus.archiver.jar.Manifest.Attribute;
import org.codehaus.plexus.util.FileUtils;

import drjoliv.fjava.adt.Either;
import drjoliv.fjava.adt.Try;
import static drjoliv.fjava.adt.Try.*;
import drjoliv.fjava.adt.Unit;
import drjoliv.fjava.io.IO;

/**
 * Goal which creates a .deb file.
 */
@Mojo( name = "build", requiresOnline = false, requiresProject = true
    , requiresDependencyResolution = ResolutionScope.RUNTIME
    , threadSafe = true, defaultPhase = LifecyclePhase.PACKAGE )
public class DebianMojo extends AbstractDebianMojo {

    public void execute() throws MojoExecutionException {
      getLog().info("debian-maven-plugin");

        Either<Exception,Unit> e =
        copyDebian()
        .semi(copyDependencies())
        .semi(createMainClassJar())
        .semi(createExecutable())
        .semi(createControlFile())
        .semi(packageDeb())
        .semi(cleanUp())
        .run();
    }

    private Try<Unit> copyDebian() {
        return Try.with(() -> {
        if (getSrcDebianFolder().isDirectory())
          FileUtils.copyDirectory(getSrcDebianFolder(), getTempFolder());
        return Unit.unit; 
        }).recoverWith(IOException.class, e -> {
        getLog().error(e.getMessage());
        return failure(e);
      });
    }

    private Try<Unit> createMainClassJar() {
        return Try.with(() -> {
          JarArchiver jar = new JarArchiver(); 
          jar.addDirectory(getClassFolder());
          Manifest man = new Manifest();
          //Manifest.Attribute applicationName = new Manifest.Attribute("Application-Name","papa Jo");
          //man.addConfiguredAttribute(applicationName);
          Manifest.Attribute mainClass = new Manifest.Attribute("Main-Class", getMainClass());
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
        getLog().error(e.getMessage());
        return failure(e);
      });
    }

    private Try<Unit> createExecutable() {
        return Try.with(() -> {
          PrintWriter writer = new PrintWriter(new FileWriter(getExecutable()));
          writer.println("#!/bin/sh");
          writer.println("exec java -jar " + "/usr/share/" + getPackageName() + "/" + getProject().getArtifactId()+"-"+getProject().getVersion()+".jar \"$@\"");
          writer.close();
          Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-xr-x");
          Files.setPosixFilePermissions(getExecutable().toPath(),perms);
          return Unit.unit; 
        }).recoverWith(IOException.class, e -> {
        getLog().error(e.getMessage());
        return failure(e);
      });
    }

    private Try<Unit> copyDependencies() {
        return Try.with(() -> {
          for(Artifact a : getArtifacts()) {
            getLog().info(a.getScope());
           FileUtils.copyFileToDirectory(a.getFile(),getPackagedLibsFolder());
          }
          return Unit.unit; 
        }).recoverWith(IOException.class, e -> {
        getLog().error(e.getMessage());
        return failure(e);
      });
    }

    private Try<Unit> packageDeb() {
        return Try.with(() -> {
          for( File f : getPackagedLibsFolder().listFiles()) {
              Files.setPosixFilePermissions(f.toPath(), PosixFilePermissions.fromString("rwxr-xr-x"));
          }
          String artifcatName = getPackageName() + "_" + getVersion() + ".deb";
          Process p = Runtime.getRuntime()
            .exec("dpkg --build debian " + artifcatName , null, getTempFolder());
          p.waitFor();
          FileUtils.copyFileToDirectory(new File(getTempFolder(),artifcatName), getBuildDirectory());

        //  File f = new File(getTempFolder(),"/debian");
        //  FileSystem fs = FileSystems.getDefault();
        //  Path p = f.toPath();
        //  UserPrincipal us = fs.getUserPrincipalLookupService().lookupPrincipalByName("root");
        //getLog().error("created us");
        //getLog().error(System.getProperty("user.name"));

        //  Files.setOwner(p, us);
        return Unit.unit; 
      }).recoverWith(FileSystemException.class, e -> {
        getLog().error(e.getMessage());
        return failure(e);
      });
    }

    private Try<Unit> createControlFile() {
        return Try.with(() -> {
          writeCtrlFile(getControlFile());
        return Unit.unit; 
      }).recoverWith(IOException.class, e -> {
        getLog().info(e.getMessage());
        return failure(e);
      }).recoverWith(FileNotFoundException.class, e -> {
        getLog().error(e.getMessage());
        return failure(e);
      });
    }

    private Try<Unit> cleanUp() {
        return Try.with(() -> {
          FileUtils.deleteDirectory(getTempFolder());
        return Unit.unit; 
      });
    }

    private void writeCtrlFile(File file) throws IOException {
      ControlFile ctrlFile = new ControlFile();
      ctrlFile.setArchitecture(getArchitecture());
      ctrlFile.setLongDecription(getLongDecription());
      ctrlFile.setMaintainerEmail(getMaintainerEmail());
      ctrlFile.setMaintainerName(getMaintainerName());
      ctrlFile.setPriority(getPriority());
      ctrlFile.setProgramName(getPackageName());
      ctrlFile.setSection(getSection());
      ctrlFile.setShortDecription(getShortDecription());
      ctrlFile.setVersionName(getVersion());
      ctrlFile.validate();
      ctrlFile.write(file);
    }
}
