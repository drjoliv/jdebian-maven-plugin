package drjoliv.plugin;


import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Goal which creates a .deb file.
 */
@Mojo( name = "debain", defaultPhase = LifecyclePhase.PROCESS_SOURCES )
public class MyMojo extends AbstractMojo {

    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true )
    private File outputDirectory;

    private File tempDir = new File(File.separator + "javaDebianTemp");

  /**
   *The name of the binary package.
   * Package names must consist only of lower case letters (a-z), digits (0-9), plus (+) and minus (-) signs, and periods (.).
   * They must be at least two characters long and must start with an alphanumeric character.
   *@see <a href="http://www.sosst.sk/doc/debian-policy/policy.html/ch-controlfields.html#s-f-Package">Package</a> 
   *
   */
    @Parameter( defaultValue = "${porject.name}", property = "packageName", required = true )
    private String packageName;

  /**
   *The version number of a package.
   *@see <a href="http://www.sosst.sk/doc/debian-policy/policy.html/ch-controlfields.html#s-f-Version">Version</a> 
   */
    @Parameter( defaultValue = "${porject.version}", property = "version", required = true )
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

    public enum Priority {REQUIRED, IMPORTANT, STANDARD, OPTIONAL, EXTRA}

  /**
   * A unique single word identifying a Debian machine architecture
   * @see <a href="http://www.sosst.sk/doc/debian-policy/policy.html/ch-controlfields.html#s-f-Architecture">Architecture</a> 
   */
    @Parameter( defaultValue = "ALL", property = "architecture", required = true )
    private Architecture architecture;

    public enum Architecture {ALL, ANY, SOURCE}

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
    @Parameter( defaultValue = "This is my long description", property = "longDecription", required = true )
    private String longDecription;

  /**
   *The synopsis or the short description of the package.
   * @see <a href="http://www.sosst.sk/doc/debian-policy/policy.html/ch-controlfields.html#s-f-Description">Description</a> 
   */
    @Parameter( defaultValue = "This is my single line synopsis.", property = "shortDecription", required = true )
    private String shortDecription;

  /**
   *
   */
    @Parameter( defaultValue = "false", property = "gnome", required = true )
    private boolean gnome;

    public void execute() throws MojoExecutionException {}
}
