![Variant Logo](http://www.getvariant.com/wp-content/uploads/2016/07/VariantLogoSquare-100.png)

# Servlet Adapter for Variant Java Client
### Release 0.10.0

### [Documentation](https://www.getvariant.com/resources/docs/0-10/clients/variant-java-client/#section-5.1) | [Javadoc](https://getvariant.github.io/variant-java-servlet-adapter/)

#### Requires: 
* [Variant Java Client 0.10.0](https://www.getvariant.com/resources/docs/0-10/clients/variant-java-client/)
* [Variant AIM Server 0.10](http://www.getvariant.com/resources/docs/0-10/application-iteration-server/user-guide/) 
* Java Servlet API 3.0 or later
* Java 8 or later.

## 1. Introduction

A Java host application communicates with an instance of [Variant AIM Server](http://www.getvariant.com/resources/docs/0-9/experience-server/user-guide/) via the  [Variant Java Client](https://www.getvariant.com/resources/docs/0-9/clients/variant-java-client/), a general purpose Java client cliebrary, which makes no assumption about the host application's operational details, other than it run on a JVM and hence can consume a Java API. This flexibility comes at the expense of some deferred dependencies, such as a mechanism for tracking Variant session ID between state request. These deferred dependencies are provided by stack-specific adapters, such as the Servlet Adapter discussed in this document. It is intened to be used by those host applications, which run in servlet containers, such as Tomcat.

Servlet adapter wraps the general Variant Java client with a higher level API, which re-writes all environment-dependent method signatures in terms of the familiar servlet objects `HttpServletRequest` and `HttpServletResponse`. The servlet adapter preserves all of the underlying Java client’s functionality and comes with out-of-the-box implementations of all [environment-dependent classes](https://www.getvariant.com/resources/docs/0-9/clients/variant-java-client/#section-3.4).

Servlet adapter consists of the following components:
* [ServletVariantClient](https://github.com/getvariant/variant-java-servlet-adapter/blob/master/src/main/java/com/variant/client/servlet/ServletVariantClient.java) and related classes mirror the corresponding classes of the underlying general Java Client.
* HTTP Cookie based implementations of the [targeting tracker](https://getvariant.github.io/variant-java-servlet-adapter/com/variant/client/servlet/TargetingTrackerHttpCookie.html) and the [session ID tracker](https://getvariant.github.io/variant-java-servlet-adapter/com/variant/client/servlet/SessionIdTrackerHttpCookie.html).
* Updated [configuration file](https://github.com/getvariant/variant-java-servlet-adapter/blob/master/variant.conf).
* [VariantFilter](https://getvariant.github.io/variant-java-servlet-adapter/com/variant/client/servlet/VariantFilter.html) bootstraps the servlet adapter and underlying Variant client as a servlet filter. Implements eager instrumentation.

## 2. Installation
### 2.1. Classpath Installation

The servlet adapter JAR file and its two transitive dependencies can be found in this repository's [/lib](https://github.com/getvariant/variant-java-servlet-adapter/tree/master/lib) directory. Add all three JAR files on your host application's classpath.

Note that these libraries in turn have the following transitive dependencies:

1. Apache [HTTP Client (4.5+)](https://hc.apache.org/httpcomponents-client-4.5.x/index.html) library. 
2. [Simple Logging Facade for Java (1.7+)](https://www.slf4j.org/) library. 
2. [Typesafe Config (1.2+)](https://github.com/typesafehub/config) library. 

Download these dependent libraries and add them to your host application classpath as well, if you don't already have them.

### 2.2. Maven Build Installation

__∎ Install private dependencies__ 

Add the servlet adapter JAR and its two private dependent JARs to your local Maven repository:

```shell
% mvn install:install-file -Dfile=lib/variant-java-client-0.9.3.jar -DgroupId=com.variant -DartifactId=variant-java-client -Dversion=0.9.3 -Dpackaging=jar

% mvn install:install-file -Dfile=lib/variant-core-0.9.3.jar -DgroupId=com.variant -DartifactId=variant-core -Dversion=0.9.3 -Dpackaging=jar

% mvn install:install-file -Dfile=lib/variant-java-client-servlet-adapter-0.9.3.jar -DgroupId=com.variant -DartifactId=variant-java-client-servlet-adapter -Dversion=0.9.3 -Dpackaging=jar
```
__∎ Add dependencies to your build__

Add the following dependencies to your host application's `pom.xml` file (copied from this project's [pom.xml](https://github.com/getvariant/variant-java-servlet-adapter/blob/master/pom.xml)

```
<dependency>
   <groupId>com.variant</groupId>
   <artifactId>java-client-servlet-adapter</artifactId>
   <version>0.9.3</version>
</dependency>

<dependency>
   <groupId>com.variant</groupId>
   <artifactId>java-client</artifactId>
   <version>0.9.3</version>
</dependency>

<dependency>
   <groupId>com.variant</groupId>
   <artifactId>variant-core</artifactId>
   <version>0.9.3</version>
</dependency>

<dependency>
   <groupId>org.apache.httpcomponents</groupId>
   <artifactId>httpclient</artifactId>
   <version>4.5.1</version>
</dependency>

<dependency>
   <groupId>com.typesafe</groupId>
   <artifactId>config</artifactId>
   <version>1.2.1</version>
</dependency>

<dependency>
   <groupId>org.slf4j</groupId>
   <artifactId>slf4j-api</artifactId>
   <version>1.7.12</version>
</dependency>
```

## 3. Configuration

The following configuration properties must be set in your application's variant.conf:
```
session.id.tracker.class.name = "com.variant.client.servlet.SessionIdTrackerHttpCookie"
targeting.tracker.class.name = "com.variant.client.servlet.TargetingTrackerHttpCookie"
```
Refer to the [Variant Java Client User Guide](https://www.getvariant.com/resources/docs/0-9/clients/variant-java-client/#section-2.2) for more information on how to configure your Variant Java Client.

## 4. Building From Source with Maven

__∎ Clone this repository to your local system.__

```
% git clone https://github.com/getvariant/variant-java-servlet-adapter.git
```

__∎ Install private transitive dependencies.__

This project depends on the following transitive dependencies, found in the [/lib](https://github.com/getvariant/variant-java-servlet-adapter/tree/master/lib) directory.

| File        | Description           | 
| ------------- | ------------- | 
| `variant-java-client-0.9.3.jar` | The general Variant Java client. The servlet adapter runs on top of it. | 
| `variant-core-0.9.3.jar` | Variant Java Client's transitive dependency. | 

Add these libraries to your corporate Maven repository or to your local repository:

```shell
% mvn install:install-file -Dfile=lib/variant-java-client-0.9.3.jar -DgroupId=com.variant -DartifactId=variant-java-client -Dversion=0.9.3 -Dpackaging=jar

% mvn install:install-file -Dfile=lib/variant-core-0.9.3.jar -DgroupId=com.variant -DartifactId=variant-core -Dversion=0.9.3 -Dpackaging=jar
```
__∎ Package the Servlet Adapter JAR__
```shell
% mvn clean package
```
this will create the `java-client-servlet-adapter-0.9.3.jar` file in the `/target` directory.
