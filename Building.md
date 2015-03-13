# Building Rococoa #
## Prequisites ##

  * Maven 2 (tested with Maven 2.1.0)
  * XCode 3.0 or later.

Builds work on Mac OS X 10.5+ on Intel. Building on PPC shows test failures with `long` return values and tests must be omitted using `-Dmaven.test.skip`.

## Command-line Build Steps ##

[Checkout](http://code.google.com/p/rococoa/source/checkout) from Subversion or download the [latest release](http://code.google.com/p/rococoa/downloads/list).

Build and test with
```
mvn test
```
If all is successful, you should have a screenful of output, culminating in
```
Results :
[surefire] Tests run: 58, Failures: 0, Errors: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESSFUL
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 1 minute 46 seconds
[INFO] Finished at: Fri Feb 22 11:39:09 GMT 2008
[INFO] Final Memory: 5M/10M
[INFO] ------------------------------------------------------------------------
```

~~For some reason the native compile step takes an age to fire up xcodebuild on
my Leopard MacBook, but then XCode itself takes an age to start as well. Your
mileage may vary.~~

## Other interesting Maven goals include ##

  * `mvn clean`
  * `mvn site` generate javadoc and stuff
  * `mvn package` to build the distribution.

## Developing using Eclipse ##

To generate an Eclipse project for the Maven build `mvn eclipse:eclipse`.

This will generate the .classpath and .project files for an Eclipse project,
that you can import into a workspace. The dependent libraries will be specified
relative to a <em>Classpath Variable</em> ```
M2_REPO```. This should be set (
`Eclipse/Preferences.../Java/Build Path/Classpath Variables`) to
`~/.m2/repository</code>`.

<p>If all is well the tests in <pre><code>rococoa/src/test/java</code></pre> should pass when<br>
run with Eclipse's JUnit test runner.<br>
<br>
<h2>Developing using JetBrains IntelliJ Idea</h2>

Make sure the Maven Plugin is enabled. Then choose<br>
<ul><li>New Project. Import project from external model and select <i>Maven</i>. The modules should be automatically recognized and added to your project.<br>
</li><li>To run test cases from within the project, set the working directory in the configuration panel to the path of the rococoa-core module. Otherwise the rococoa.dylib will not be found.