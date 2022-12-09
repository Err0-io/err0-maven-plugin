# err0 maven plugin

## Add to your project's pom.xml: 

```
       <plugins>
        ...
        <plugin>
          <groupId>io.err0</groupId>
          <artifactId>err0-maven-plugin</artifactId>
          <version>1.0-SNAPSHOT</version>
          <configuration>
            <token>/path/to/token.json</token>
          </configuration>
        </plugin>
        ...
      </plugins>
```

putting the token json file path for your err0 token.

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