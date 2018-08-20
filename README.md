# jDebian
[![](https://jitpack.io/v/drjoliv/jdebian-maven-plugin.svg)](https://jitpack.io/#drjoliv/jdebian-maven-plugin)

<hr/>

A Maven Plugin for creating .deb files from Java applications.

jDebian creates an executable Jar file from your Java code and wraps it up in a .deb file. Additionally all of the RUNTIME dependencies located within your pom.xml are added to the classpath of the the exectuable Jar and are also wrapped up nice and tight within the .deb file.

Below is an example of adding jDebian to your pom.xml
```xml

...
  <packaging>deb</packaging>

...
<repositories>
  <repository>
      <id>jitpack.io</id>
      <url>https://jitpack.io</url>
  </repository>
</repositories>

...
  <build>
      <plugins>
        <plugin>
          <groupId>com.github.drjoliv</groupId>
          <artifactId>jdebian-maven-plugin</artifactId>
          <version>0.1-SNAPSHOT</version>
          <!-- required configuration parameters-->
          <configuration>
            <!-- Detailed description of the application. -->
            <longDecription>A long description of the jDebian plugin example.</longDecription>  

            <!-- Short description of application no more that 60 characters. -->
            <shortDecription>A short description of the jDebian plugin example.</shortDecription> 

            <!-- Email of the application maintainer. -->
            <maintainerEmail>drjoliv@gmail.com</maintainerEmail>

            <!-- Name of the application maintainer.-->
            <maintainerName>Desonte Jolivet</maintainerName> 

            <!--Executable JAR entry point-->
            <mainClass>com.github.drjoliv.App</mainClass>
          </configuration>

          <!-- Very IMPORTANT enables custom lifecycle -->
          <extensions>true</extensions>
        </plugin>
      </plugins>
  </build>
...
```

# Usage

## Creating .deb archive

The below exampls use this [example maven project]().

* mvn package

[![asciicast](https://asciinema.org/a/Oh6EGuPGq2cu0QndzVkiwbZ4k.png)](https://asciinema.org/a/Oh6EGuPGq2cu0QndzVkiwbZ4k)

## Install .deb application to your machine.

* mvn install

[![asciicast](https://asciinema.org/a/ySzOkl0Y1E7qeQ4cvJ60tYBX3.png)](https://asciinema.org/a/ySzOkl0Y1E7qeQ4cvJ60tYBX3)

# Dependency

* dpkg
