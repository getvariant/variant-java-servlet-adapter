![Variant Logo](http://www.getvariant.com/wp-content/uploads/2016/07/VariantLogoSquare-100.png)

# Servlet Adapter for Variant Java Client
## Wrapper API for Variant Java Client, suitable for host application written on top of the Servlet API. Enables integration with Variant Experiment Server.
### Release 1.0.0

[__Documentation__](http://www.getvariant.com/docs/0-8/clients/variant-java-client/#section-3) | [__Javadoc__](https://getvariant.github.io/variant-java-servlet-adapter/)

#### Requires: 
* Variant Java Client 0.8.x 
* Variant Experiment Server 0.8.x 
* Java Servlet API 2.4 or later 
* Java 7 or later.

## 1. Introduction

A Java application, in order to instrument online controlled experiments or feature toggles, must integratie with Variant Experiment Server. Any Java host application may communicate with Variant server via the [bare Variant Java client](http://www.getvariant.com/docs/0-8/clients/variant-java-client/#section-2). However, many Java Web applications are written on top of the Servlet API, either directly or via a servlet-based framework, such as Spring MVC. Such applications should take advantage of this servlet adapter, instead of coding directly to the bare API.

The servlet adapter wraps the bare Java client with a higher level API, which re-writes environment-dependent method signatures in terms of familiar servlet objects, like <span class="variant-code">HttpServletRequest</span>. The servlet adapter preserves 100% of the bare client’s functionality and comes with out-of-the-box implementations of all [environment-dependent classes](http://www.getvariant.com/docs/0-8/clients/variant-java-client/#section-2.4).

Variant servlet adapter for the Java client contains the following three components:
* [VariantFilter](https://getvariant.github.io/variant-java-servlet-adapter/com/variant/client/servlet/VariantFilter.html) bootstraps the servlet adapter and underlying Variant client and implements all the core functionality a simple Variant experiment will require. Integrates with the host application as a servlet filter.
* Implementations of all environment-dependent classes in terms of servlet API objects. 
* Updated [configuration file](https://github.com/getvariant/variant-java-servlet-adapter/blob/master/src/main/resources/variant.conf).

## 2. Classpath Installation

The servlet adapter JAR file and its two transitive dependencies can be found in this repository in the [/lib](https://github.com/getvariant/variant-java-servlet-adapter/tree/master/lib) directory. Add all three JAR files on your host application's classpath.

Note that these libraries in turn have the following transitive dependencies:

1. Apache Commons [Lang (3.4+)](https://commons.apache.org/proper/commons-lang/) and [IO (2.4+)](https://commons.apache.org/proper/commons-io/) libraries. 
2. Apache Components [HTTP Client (4.5+)](https://hc.apache.org/httpcomponents-client-4.5.x/index.html) library. 
2. [Simple Logging Facade for Java (1.7+)](https://www.slf4j.org/) library. 
2. [Typesafe Config (1.2+)](https://github.com/typesafehub/config) library. 

Download these dependent libraries and add them to your host application classpath.

## 3. Adding Servlet Adapter to Host Application's Maven Build

__1. Download dependent libraries:__
The pre-bult JAR and its proprietary transitive dependencies an be found in this repository in the [/lib](https://github.com/getvariant/variant-java-servlet-adapter/tree/master/lib) directory. Download these JAR files.

__2. Install private dependencies:__ 
Add the downloaded files to your corporate Maven repository or to your local repository:

```shell
% mvn install:install-file -Dfile=/path/to/variant-java-client-0.8.0.jar -DgroupId=com.variant -DartifactId=java-client -Dversion=<release> -Dpackaging=jar

% mvn install:install-file -Dfile=/path/to/variant-core-0.8.0.jar -DgroupId=com.variant -DartifactId=variant-core -Dversion=<release> -Dpackaging=jar

% mvn install:install-file -Dfile=/path/to/java-client-servlet-adapter-1.0.0.jar -DgroupId=com.variant -DartifactId=java-client-servlet-adapter -Dversion=1.0.0 -Dpackaging=jar
```
__3. Add dependencies to your build:__
Add the following dependencies to your host application's `pom.xml` file (copied from this project's [pom.xml](https://github.com/getvariant/variant-java-servlet-adapter/blob/master/pom.xml)

```
<dependency>
   <groupId>com.variant</groupId>
   <artifactId>java-client-servlet-adapter</artifactId>
   <version>1.0.0</version>
</dependency>

<dependency>
   <groupId>com.variant</groupId>
   <artifactId>java-client</artifactId>
   <version>[0.8,)</version>
</dependency>

<dependency>
   <groupId>com.variant</groupId>
   <artifactId>variant-core</artifactId>
   <version>[0.8,)</version>
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
   <groupId>org.apache.commons</groupId>
   <artifactId>commons-lang3</artifactId>
   <version>3.4</version>
</dependency>

<dependency>
   <groupId>org.slf4j</groupId>
   <artifactId>slf4j-api</artifactId>
   <version>1.7.12</version>
</dependency>
```

## 4. Building From Source with Maven

__1. Clone this repository to your local system.__

```
% git clone https://github.com/getvariant/variant-java-servlet-adapter.git
```

__2. Install private transitive dependencies.__

This project depends on the following transitive dependencies, found in the [/lib](https://github.com/getvariant/variant-java-servlet-adapter/tree/master/lib) directory.

| File        | Description           | 
| ------------- | ------------- | 
| `variant-java-client-0.8.0.jar` | Variant Java client, a.k.a. the bare Java client. The servlet adapter runs on top of it. | 
| `variant-core-0.8.0.jar` | Dependent Variant core library. Contains objects shared between the client and the server code bases. | 

Add these libraries to your corporate Maven repository or to your local repository:

```shell
% mvn install:install-file -Dfile=/path/to/variant-java-client-0.8.0.jar -DgroupId=com.variant -DartifactId=java-client -Dversion=<release> -Dpackaging=jar

% mvn install:install-file -Dfile=/path/to/variant-core-0.8.0.jar -DgroupId=com.variant -DartifactId=variant-core -Dversion=<release> -Dpackaging=jar
```
__3. Build the Servlet Adapter__
```shell
% mvn clean package
```
this will create the `java-client-servlet-adapter-0.8.0.jar` file in the `/target` directory. You must place it on your host application's runtime classpath.

## 5 Configuration
Your applicaiton must use the [config file, which comes with this project](https://github.com/getvariant/variant-java-servlet-adapter/blob/master/src/main/resources/variant.conf). See [Variant Java Client User Guide](http://www.getvariant.com/docs/0-8/clients/variant-java-client/#section-2.2) for details.


