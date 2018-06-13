package drjoliv.plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import drjoliv.plugin.AbstractDebianMojo.Architecture;
import drjoliv.plugin.AbstractDebianMojo.Priority;


public class ControlFile {
    private String programName;
    private String versionName;
    private String section;
    private Priority priority;
    private Architecture architecture;
    private String maintainerEmail;
    private String maintainerName;
    private String longDecription;
    private String shortDecription;

  /**
   * @param programName the programName to set
   */
  public void setProgramName(String programName) {
    this.programName = programName;
  }

  /**
   * @param versionName the versionName to set
   */
  public void setVersionName(String versionName) {
    this.versionName = versionName;
  }

  /**
   * @param section the section to set
   */
  public void setSection(String section) {
    this.section = section;
  }

  /**
   * @param priority the priority to set
   */
  public void setPriority(Priority priority) {
    this.priority = priority;
  }

  /**
   * @param architecture the architecture to set
   */
  public void setArchitecture(Architecture architecture) {
    this.architecture = architecture;
  }


  /**
  * @param maintainerEmail the maintainerEmail to set
  */
  public void setMaintainerEmail(String maintainerEmail) {
    this.maintainerEmail = maintainerEmail;
  }

  /**
   * @param maintainerName the maintainerName to set
   */
  public void setMaintainerName(String maintainerName) {
    this.maintainerName = maintainerName;
  }

  /**
   * @param longDecription the longDecription to set
   */
  public void setLongDecription(String longDecription) {
    this.longDecription = longDecription;
  }

  /**
   * @param shortDecription the shortDecription to set
   */
  public void setShortDecription(String shortDecription) {
    this.shortDecription = shortDecription;
  }

  public void validate() {
    // TODO Auto-generated method stub
    
  }

  public void write(File file) throws IOException {
    try(PrintWriter writer = new PrintWriter(new FileWriter(file))) {
    if(programName != null)
      writer.println("Package: " + programName);

    if(versionName != null)
      writer.println("Version: " + versionName);

    if(section != null)
      writer.println("Section: " + section);

    if(priority != null)
      writer.println("Priority: " + priority);

    if(architecture != null)
      writer.println("Architecture: " + architecture);

    if(maintainerEmail != null && maintainerName != null)
      writer.println("Maintainer: " + maintainerName + " <" + maintainerEmail + ">");

    if(shortDecription != null)
      writer.println("Description: " + shortDecription);

    if(longDecription != null)
      writer.println(" .");
      writer.println(" " + longDecription);
      writer.println(" .");
    }
  }
}
