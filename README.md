# javadebian
A Maven Plugin for creating .deb files from Java applications.

javadebian creates an executable Jar file from your Java code and wraps it up in a .deb file. Additionally all of the RUNTIME dependencies located within your pom.xml are added to the classpath of the the exectuable Jar and are also wrapped up nice and tight within the .deb file.

Sadly javadebian currently has a dependeny on dpkg, so dpkg must be installed. Hopefully this will be fixed soon.

The only phase in which javadebian needs to bind to is the package phase.

Below is an example of adding javadebian to your pom.xml
```xml
...
  <build>
    <plugins>
      <plugin>
        <groupId>drjoliv.plugin</groupId>
        <artifactId>debian-maven-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
        <executions>
          <execution>
            <id>simple test</id>
            <goals>
              <goal>build</goal>
            </goals>
            <phase>package</phase>
            <configuration>
              <!-- required configuration parameters-->
              <longDecription>...</longDecription>   <!-- Detailed description of the application. -->
              <shortDecription>...</shortDecription> <!-- Short descriptin of application no more that 60 characters. -->
              <maintainerEmail>...</maintainerEmail> <!-- Email of the application maintainer. -->
              <maintainerName>...</maintainerName>   <!-- Name of the application maintainer.-->
              <mainClass></mainClass>
            </configuration>
          </execution>
        </executions>
      </plugin>
    </plugins>
  </build>
...
```
