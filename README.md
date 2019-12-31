![Variant Logo](http://www.getvariant.com/wp-content/uploads/2016/07/VariantLogoSquare-100.png)

# Servlet Adapter for Variant Java Client
### Release 0.10.3

### [Documentation](https://www.getvariant.com/resources/docs/0-10/clients/variant-java-client/#section-5.1) | [Javadoc](https://getvariant.github.io/variant-java-servlet-adapter/)

#### Requires: 
* [Variant Java Client 0.10.3](https://www.getvariant.com/resources/docs/0-10/clients/variant-java-client/)
* [Variant AIM Server 0.10.3](http://www.getvariant.com/resources/docs/0-10/application-iteration-server/user-guide/) 
* Java Servlet API 3.0 or later
* Java 8 or later.

## 1. Introduction

A Java host application communicates with an instance of [Variant AIM Server](http://www.getvariant.com/resources/docs/0-10/application-iteration-server/user-guide/) via the  [Variant Java Client](https://www.getvariant.com/resources/docs/0-10/clients/variant-java-client/), which makes no assumption about the host application's operational details, other than it runs on a Java VM. This flexibility comes at the expense of some deferred dependencies, such as a mechanism for tracking Variant session ID between state request. These dependencies are expectd to be provided by the applicatoin programmer, familiar with the runtime operational details.

However, some of the most common applicatoin stacks are supportd via stack specific adapters. In particular, the Servlet Adapter assumes the host application to run in a servlet container, like Tomcat, discussed here. It consists of two components:

* Wrapper client API, which wraps the general purpose classes in a functionally identical servlet-aware classes, whose only difference is that they rewrites all deferred environment-dependent method signatures with those that operate on the familiar servlet objects, like `HttpServletRequest` and `HttpServletResponse`;
* Servlet-based implementation of the session ID tracker, utilizing HTTP cookies.

See [documentation](https://www.getvariant.com/resources/docs/0-10/clients/variant-java-client/#section-5.1) for further details.

## 2. Installation

The pre-built servlet adapter JAR file and the dependent Variant Java client JAR can be found in this repository's [/lib](https://github.com/getvariant/variant-java-servlet-adapter/tree/release/0.10.3/lib) directory. Add both of them to your `pom.xml`:

```
<dependency>
   <groupId>com.variant</groupId>
   <artifactId>java-client-servlet-adapter</artifactId>
   <version>0.10.3</version>
</dependency>

<dependency>
   <groupId>com.variant</groupId>
   <artifactId>java-client</artifactId>
   <version>0.10.3</version>
</dependency>
```

Note, that these libraries in turn have the following transitive dependencies:

1. Apache [HTTP Client (4.5+)](https://hc.apache.org/httpcomponents-client-4.5.x/index.html) library. 
2. [Simple Logging Facade for Java (1.7+)](https://www.slf4j.org/) library.


```
<dependency>
   <groupId>org.apache.httpcomponents</groupId>
   <artifactId>httpclient</artifactId>
   <version>4.5.1</version>
</dependency>

<dependency>
   <groupId>org.slf4j</groupId>
   <artifactId>slf4j-api</artifactId>
   <version>1.7.12</version>
</dependency>
```

## 3. Building From Source with Maven

__∎ Clone this repository to your local system.__

```
% git clone https://github.com/getvariant/variant-java-servlet-adapter.git
```

__∎ Install the dependent Variant Java client.__

```shell
% mvn install:install-file -Dfile=lib/variant-java-client-0.10.3.jar -DgroupId=com.variant -DartifactId=variant-java-client -Dversion=0.10.3 -Dpackaging=jar
```
__∎ Running unit tests__

Unit tests expect two test variation schemata to be deployed on an instance of Variant server, running at `http://localhost:5377`. Simply copy the contents of `src/test/resurces/schemata` to your server's `schemata` directory.

__∎ Package the Servlet Adapter JAR__
```shell
% mvn clean package
```
this will run the unit tests and create the `java-client-servlet-adapter-0.10.3.jar` file in the `/target` directory.  
