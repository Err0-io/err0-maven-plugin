# err0 maven plugin

Note: in the below, you should be able to omit the ```<version>``` in which case you will get the most recent version.

## Add to your project's pom.xml: 

```
       <plugins>
        ...
        <plugin>
          <groupId>io.err0</groupId>
          <artifactId>err0-maven-plugin</artifactId>
          <version>1.0.0</version>
          <configuration>
            <token>/path/to/token.json</token>
          </configuration>
        </plugin>
        ...
      </plugins>
```

putting the token json file path for your err0 token.

You can specify the directory that contains the downloaded err0agent jar file with this configuration property, the default is the project target directory.  Note: this directory must exist, we will not create it.

```
        <plugin>
          <groupId>io.err0</groupId>
          <artifactId>err0-maven-plugin</artifactId>
          <version>1.0.0</version>
          <configuration>
            <token>/path/to/token.json</token>
            <agentDir>~/.err0agent</agentDir>
          </configuration>
        </plugin>
```

You can specify the directory which is the root for the err0agent similarly, normally this should be the same directory as the .git folder, the project base directory.

```
        <plugin>
          <groupId>io.err0</groupId>
          <artifactId>err0-maven-plugin</artifactId>
          <version>1.0.0</version>
          <configuration>
            <token>/path/to/token.json</token>
            <baseDir>./src</baseDir>
          </configuration>
        </plugin>
```

## Usage

Using err0 is a manual process, it is controlled by maven,
but does not attach to the build lifecycle as this is for
"one off" insert runs and for your CI/CD system to check that
codes are canonical.

### Insert new error codes into logs and exceptions:

```
$ mvn io.err0:err0-maven-plugin:insert
```

### Check that codes are canonical and commit to err0

```
$ mvn io.err0:err0-maven-plugin:check
```
