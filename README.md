# Automacent Test Framework ![Java CI with Maven](https://github.com/sighil/automacent/workflows/Java%20CI%20with%20Maven/badge.svg)

Test automation framework based on TestNG to write test code with a host of helpful utilities and enhanced reporting structure. The framework primarily focusses on UI test case development with Selenium included as a direct dependency. That being said, the framework can also used for writing API tests, database tests and unit tests.

## Features
Below meantioned are some of the salient features of the framework
- Three tier structured project management using Tests, Steps and Pages (In case of Selenium tests)
- Auto logging of execution flow
- Slow down execution
- Retry tests
- Repeat test cases in a loop for a set time period or number of times
- HTML report using ReportNG
- XML Report
- Build in REST API result publish interface

Selenium Specific features

- Automatic driver management
- CHROME / FF / IE support
- Screenshot Management
- Manage test timeouts
- Handle and resolve common selenium issues like test failures due to flakiness

> For documentation and entire feature list visit [GitHub Automacent Framework Wiki](https://github.com/sighil/automacent/wiki)

## Stable version
Add below dependency to maven pom.xml to get started with the released stable version
```
<dependency>
    <groupId>com.automacent</groupId>
    <artifactId>automacent-fwk</artifactId>
    <version>1.3</version>
</dependency>
```
## Snapshot version
Add below dependency to maven pom.xml to get started with the snapshot version
```
<dependency>
    <groupId>com.automacent</groupId>
    <artifactId>automacent-fwk</artifactId>
    <version>1.4-SNAPSHOT</version>
</dependency>
```
You have to add the below repository to access the snapshot version
```
<repositories>
  <repository>
    <id>SONATYPE_OSS</id>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
  </repository>
</repositories>
```
