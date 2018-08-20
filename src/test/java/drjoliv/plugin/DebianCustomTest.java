package drjoliv.plugin;


import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.WithoutMojo;

import drjoliv.plugin.AbstractDebianMojo.*;
import org.junit.Rule;
import static org.junit.Assert.*;
import org.junit.Test;


import java.io.File;

public class DebianCustomTest extends AbstractMojoTestCase {

    @Test
    public void testProperty()
            throws Exception {
      File pom = getPom("/pom-default.xml");
      nullPom(pom);

      Package deb = ( Package ) lookupMojo( "package" , pom);
      nullMojo(deb);

      assertEquals(Architecture.all, getVariableValueFromObject(deb,"architecture")); //architecture
      assertEquals("A very winded description",getVariableValueFromObject(deb,"longDecription")); //longDecription
      assertEquals("com.my.MainClass",getVariableValueFromObject(deb,"mainClass")); //mainClass
      assertEquals("drjoliv@gmail.com",getVariableValueFromObject(deb,"maintainerEmail")); //maintainerEmail
      assertEquals("drjoliv",getVariableValueFromObject(deb,"maintainerName")); //maintainerName
      assertEquals("warp",getVariableValueFromObject(deb,"packageName")); //packageName
      assertEquals(Priority.OPTIONAL,getVariableValueFromObject(deb,"priority")); //priority
      assertEquals("web",getVariableValueFromObject(deb,"section")); //section
      assertEquals("A short description.",getVariableValueFromObject(deb,"shortDecription")); //shortDecription
      assertEquals("0.0.1",getVariableValueFromObject(deb,"version")); //version
    }


    public static File getPom(String fileName) throws Exception {
      return new File(DebianCustomTest.class.getResource(fileName).toURI());
    }

    public static File getPomBaseDir(String fileName) throws Exception {
      return null;
      //return new File(DebianCustomTest.class.getResource(fileName).toURI());
    }

    public static void nullPom(File pom) {
      assertNotNull("Could not find pom.", pom);
    }

    public static void nullMojo(AbstractMojo mojo) {
      assertNotNull("Could not find Mojo.", mojo);
    }

}

