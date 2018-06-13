package drjoliv.plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import drjoliv.fjava.adt.FList;

public abstract class AbstractDebianMojo extends AbstractMojo{
  /**
   *The name of the binary package.
   * Package names must consist only of lower case letters (a-z), digits (0-9), plus (+) and minus (-) signs, and periods (.).
   * They must be at least two characters long and must start with an alphanumeric character.
   *@see <a href="http://www.sosst.sk/doc/debian-policy/policy.html/ch-controlfields.html#s-f-Package">Package</a> 
   *
   */
    @Parameter(defaultValue="${project.name}", required = true)
    private String packageName;

    @Parameter(defaultValue="${project.build.directory}", required = true, readonly = true)
    private File buildDirectory;

    @Parameter(defaultValue="${project.basedir}", required = true, readonly = true)
    private File baseDir;

    @Parameter(defaultValue="${project.build.outputDirectory}", required = true, readonly = true)
    private File buildOutputDir;

  /**
   *The version number of a package.
   *@see <a href="http://www.sosst.sk/doc/debian-policy/policy.html/ch-controlfields.html#s-f-Version">Version</a> 
   */
    @Parameter( defaultValue = "${project.version}", required = true , readonly = true)
    private String version;

  /**
   *This field specifies an application area into which the package has been classified.
   * @see <a href="http://www.sosst.sk/doc/debian-policy/policy.html/ch-controlfields.html#s-f-Section">Section</a> 
   */
    @Parameter( property = "section", required = false )
    private String section;

  /**
   * This field represents how important that it is that the user have the package installed.
   * @see <a href="http://www.sosst.sk/doc/debian-policy/policy.html/ch-controlfields.html#s-f-Priority">Priority</a> 
   */
    @Parameter( property = "priority", required = false )
    private Priority priority;

    @Parameter( property = "mainClass", required = true )
    private String mainClass;

    public enum Priority {REQUIRED, IMPORTANT, STANDARD, OPTIONAL, EXTRA}

  /**
   * A unique single word identifying a Debian machine architecture
   * @see <a href="http://www.sosst.sk/doc/debian-policy/policy.html/ch-controlfields.html#s-f-Architecture">Architecture</a> 
   */
    @Parameter( defaultValue = "all", property = "architecture", required = true )
    private Architecture architecture;

    @Parameter( defaultValue = "${project}")
    private MavenProject project;

    public enum Architecture {all, any, source}

  /**
   * The package maintainer's name.
   * @see <a href="http://www.sosst.sk/doc/debian-policy/policy.html/ch-controlfields.html#s-f-Maintainer">Maintainer</a> 
   */
    @Parameter( property = "maintainerName", required = true )
    private String maintainerName;

  /**
   * The package maintainer's email.
   * @see <a href="http://www.sosst.sk/doc/debian-policy/policy.html/ch-controlfields.html#s-f-Maintainer">Maintainer</a> 
   */
    @Parameter( property = "maintainerEmail", required = true )
    private String maintainerEmail;

  /**
   * A long description of the package.
   * @see <a href="http://www.sosst.sk/doc/debian-policy/policy.html/ch-controlfields.html#s-f-Description">Description</a> 
   */
    @Parameter( property = "longDecription", required = true )
    private String longDecription;

  /**
   *The synopsis or the short description of the package.
   * @see <a href="http://www.sosst.sk/doc/debian-policy/policy.html/ch-controlfields.html#s-f-Description">Description</a> 
   */
    @Parameter( property = "shortDecription", required = true )
    private String shortDecription;

    @Parameter( defaultValue = "${project.resources}", readonly = true,  required = true )
    private List<Resource> resources;

  /**
   * @return the packageName
   */
  public String getPackageName() {
    //TODO sanatize package name.
    return packageName;
  }

  /**
  * @return the buildDirectory
  */
  public File getBuildDirectory() {
    return buildDirectory;
  }

  /**
  * @return the baseDir
  */
  public File getBaseDir() {
    return baseDir;
  }

  /**
  * @return the version
  */
  public String getVersion() {
    return version;
  }

  /**
  * @return the section
  */
  public String getSection() {
    return section;
  }

  /**
   * @return the priority
   */
  public Priority getPriority() {
    return priority;
  }

  /**
  * @return the mainClass
  */
  public String getMainClass() {
    return mainClass;
  }

  /**
   * @param mainClass the mainClass to set
   */
  public void setMainClass(String mainClass) {
    this.mainClass = mainClass;
  }

  /**
   * @return the architecture
   */
  public Architecture getArchitecture() {
    return architecture;
  }

  /**
  * @return the project
  */
  public MavenProject getProject() {
    return project;
  }

  /**
   * @param project the project to set
   */
  public void setProject(MavenProject project) {
    this.project = project;
  }

  /**
  * @return the maintainerName
  */
  public String getMaintainerName() {
    return maintainerName;
  }

  /**
  * @return the maintainerEmail
  */
  public String getMaintainerEmail() {
    return maintainerEmail;
  }

  /**
   * @return the longDecription
   */
  public String getLongDecription() {
    return longDecription;
  }

  /**
   * @return the shortDecription
   */
  public String getShortDecription() {
    return shortDecription;
  }

  public File getSrcDebianFolder() {
      return new File(getBaseDir(), "/src/debian");
  }

  public FList<Artifact> getArtifacts() {
    return FList.fromCollection(project.getArtifacts());
  }

  public Artifact getArtifact() {
    return project.getArtifact();
  }


  public File getTempFolder() {
    File f = new File(getBuildDirectory(),"/temp");
    if(!f.isDirectory())
      f.mkdirs();
    return f;
  }

  public File getUsrFolder() {
    File f = new File(getTempFolder(),"/debian/usr");
    if(!f.isDirectory()) {
      f.mkdirs();
      try {
        Files.setPosixFilePermissions(f.toPath(), PosixFilePermissions.fromString("rwxr-xr-x"));
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }
    return f;
  }

  public File getLibFolder() {
    File f = new File(getUsrFolder(),"/share");
    if(!f.isDirectory()) {
      f.mkdirs();
      try {
        Files.setPosixFilePermissions(f.toPath(), PosixFilePermissions.fromString("rwxr-xr-x"));
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }
    return f;
  }

  public File getPackagedLibsFolder() {
    File f = new File(getLibFolder(),getPackageName());
    if(!f.isDirectory()) {
      f.mkdirs();
      try {
        Files.setPosixFilePermissions(f.toPath(), PosixFilePermissions.fromString("rwxr-xr-x"));
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }
    return f;
  }

  public File getBinFolder() {
    File f = new File(getUsrFolder(),"/bin");
    if(!f.isDirectory()) {
      f.mkdirs();
    try {
      Files.setPosixFilePermissions(f.toPath(), PosixFilePermissions.fromString("rwxr-xr-x"));
    } catch (IOException e1) {
      e1.printStackTrace();
    }

    }
    return f;
  }

  public File getClassFolder(){
    return buildOutputDir;
  }

  public File getDebianFolder() {
    File f = new File(getTempFolder(), "/debian");
    if(!f.isDirectory())
      f.mkdirs();
    return f;
  }

  public File getDEBIANFolder() {
    File f = new File(getDebianFolder(), "/DEBIAN");
    if(!f.isDirectory())
      f.mkdirs();
    return f;
  }

  public File getControlFile() {
    return new File(getDEBIANFolder(), "control");
  }

  public File getExecutable() {
    return new File(getBinFolder(), getPackageName());
  }
}
