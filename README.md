![Variant Logo](http://www.getvariant.com/wp-content/uploads/2016/07/VariantLogoSquare-100.png)

# Servlet Adapter for Variant Java Client
## Wrapper API for Variant Java Client, suitable for host application written on top of the Servlet API.
### Release 1.0.0

#### Requires: Variant Client 0.8.x, Java Servlet API 2.4 or later, Java 7 or later.

[__Download__](https://github.com/getvariant/variant-java-servlet-adapter/tree/master/lib) | [__Documentation__](http://www.getvariant.com/docs/0-8/clients/variant-java-client/#section-3) | [__Javadoc__](https://getvariant.github.io/variant-java-servlet-adapter/)

## 1. Introduction

Many Java Web applications are written on top of the Servlet API, either directly or via a servlet-based framework, such as Spring MVC. Such applications should take advantage of this servlet adapter, instead of coding directly to the [bare Variant Java client](http://www.getvariant.com/docs/0-8/clients/variant-java-client/#section-2). 

The servlet adapter wraps the bare Java client with a higher level client library, which re-writes environment-dependent function signatures in terms of familiar servlet objects, like <span class="variant-code">HttpServletRequest</span>. The servlet adapter preserves 100% of the bare client’s functionality and comes with out-of-the-box implementations of all [environment-dependent classes](http://www.getvariant.com/docs/0-8/clients/variant-java-client/#section-2.4).

Variant servlet adapter for the Java client contains the following two components:
* [VariantFilter](https://getvariant.github.io/variant-java-servlet-adapter/com/variant/client/servlet/VariantFilter.html) bootstraps the underlying Variant client and implements all the core functionality a simple Variant experiment will require. Integrates with the host application as a servlet filter.
* Re-implementations of all environment-dependent classes in terms of servlet API objects. 

## 2. Classpath Installation

The servlet adapter JAR file and its two transitive dependencies can be found in this repository in the [/lib](https://github.com/getvariant/variant-java-servlet-adapter/tree/master/lib) directory. Add all three JAR files on your host application's classpath.

Note that these libraries in turn have the following transitive dependencies:

1. Apache Commons [Lang (3.4+)](https://commons.apache.org/proper/commons-lang/) and [IO (2.4+)](https://commons.apache.org/proper/commons-io/) libraries. 
2. Apache Components [HTTP Client (4.5+)](https://hc.apache.org/httpcomponents-client-4.5.x/index.html) library. 
2. [Simple Logging Facade for Java (1.7+)](https://www.slf4j.org/) library. 
2. [Typesafe Config (1.2+)](https://github.com/typesafehub/config) library. 

Download these dependent libraries and add them to your host application classpath.

## 3. Building from Sources with Maven

1. The pre-bult JAR and its proprietary transitive dependencies an be found in this repository in the [/lib](https://github.com/getvariant/variant-java-servlet-adapter/tree/master/lib) directory. Download these JAR files.

2. Add the downloaded files to your corporate Maven repository or to your local repository (replacing `<release>` with the particular version number you're installing, e.g. `0.7.1`):

```shell
% mvn install:install-file -Dfile=/path/to/variant-java-client-<release>.jar -DgroupId=com.variant -DartifactId=java-client -Dversion=<release> -Dpackaging=jar

% mvn install:install-file -Dfile=/path/to/variant-core-<release>.jar -DgroupId=com.variant -DartifactId=variant-core -Dversion=<release> -Dpackaging=jar

% mvn install:install-file -Dfile=/path/to/java-client-servlet-adapter-1.0.0.jar -DgroupId=com.variant -DartifactId=java-client-servlet-adapter -Dversion=1.0.0 -Dpackaging=jar
```
3. Add the following dependency definitions to your host application's `pom.xml` file:

```
<dependency>
   <groupId>com.variant</groupId>
   <artifactId>java-client-servlet-adapter</artifactId>
   <version>1.0.0</version>
</dependency>

<dependency>
   <groupId>com.variant</groupId>
   <artifactId>java-client</artifactId>
   <version>[0.7,)</version>
</dependency>

<dependency>
   <groupId>com.variant</groupId>
   <artifactId>variant-core</artifactId>
   <version>[0.7,)</version>
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

## 4. Building From Source

### 4.1 Install Variant Java Client

1. [Download Variant Java client software](http://www.getvariant.com/downloads).

2. Unpack the software archive:

```shell
% unzip /path/to/variant-java-<release>.zip
```
This will inflate the following artifacts:

| File        | Description           | 
| ------------- | ------------- | 
| `variant-java-client-<release>.jar` | Variant Java client, a.k.a. the bare Java client. The servlet adapter runs on top of it. | 
| `variant-core-<release>.jar` | Dependent Variant core library. Contains objects shared between the client and the server code bases. | 
| variant.conf | Sample client configuration file containing all default settings. To override any of the defaults, change their values in this file and place it on the host application's classpath. |

2. Install the two JARs above into your local repository (replacing `<release>` with the particular version number you're installing, e.g. `0.7.1`):

```shell
% mvn install:install-file -Dfile=/path/to/variant-java-client-<release>.jar -DgroupId=com.variant -DartifactId=java-client -Dversion=<release> -Dpackaging=jar

% mvn install:install-file -Dfile=/path/to/variant-core-<release>.jar -DgroupId=com.variant -DartifactId=variant-core -Dversion=<release> -Dpackaging=jar
```

Variant Java client has a small set of external transitive dependencies, which are not included in the distribution:

### 4.2 Build the Servlet Adapter
```shell
% mvn clean package
```
this will create the `java-client-servlet-adapter-<release>.jar` file in the 'target' directory. You must place it on your host application's runtime classpath.

Alternatively, if your host applciation uses Maven, install the file in your local Maven repository:
```shell
% mvn clean install
```


Updated on 19 July 2017 for release 1.0.0.
