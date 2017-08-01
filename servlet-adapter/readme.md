# Servlet Adapter for Variant Java Client
## Wrapper API around Variant Java Client suitable for host application written on top of the Servlet API.
### Release 1.0.0
#### Requires: Java 7 or later, Java Servlet API 2.4 or later, Variant Client 0.7

[__Documentation__](http://www.getvariant.com/docs/0-7/clients/variant-java-client/#section-3) | [__Javadoc__](https://getvariant.github.io/variant-java-servlet-adapter/)

## 1. Introduction

Most Java Web applications are written on top of the Servlet API, either directly or via a servlet-based framework, such as Struts, Spring or Lift. Such applications, instead of coding directly to Variant Java client, should take advantage of this servlet adapter. 

The servlet adapter wraps the bare Java client with a higher level client library, which re-writes environment-dependent function signatures in terms of familiar servlet objects, like <span class="variant-code">HttpServletRequest</span>. The servlet adapter preserves 100% of the bare client’s functionality and comes with out-of-the-box implementations of all environment-dependent classes.

Variant servlet adapter for the Java client contains the following three components:
* [VariantFilter](https://getvariant.github.io/variant-java-servlet-adapter/com/variant/client/servlet/VariantFilter.html) bootstraps the underlying Variant client and implements all the core functionality a simple Variant experiment will require. Integrates with the host application as a servlet filter.
* Implementations of all environment-dependent in terms of servlet API objects. 
* [Servlet adapter wrapper API](https://github.com/getvariant/variant-java-servlet-adapter/tree/master/servlet-adapter) around the bare Java client replaces environment contingent method signatures with equivalent ones expressed in terms of the Servlet API classes.

## 2. Building Servlet Adapter

### 2.1 Install Variant Java Client

1. [Download Variant Java client software](http://www.getvariant.com/downloads).

2. Unpack the software archive:

```shell
% unzip /path/to/variant-java-<release>.zip
```
This will inflate the following artifacts:

| File        | Description           | 
| ------------- | ------------- | 
| variant-java-client-<release>.jar | Variant Java client, a.k.a. the bare Java client. The servlet adapter runs on top of it. | 
| variant-core-<release>.jar | Dependent Variant core library. Contains objects shared between the client and the server code bases. | 
| variant.conf | Sample client configuration file containing all default settings. To override any of the defaults, change their values in this file and place it on the host application's classpath. |

2. Install the two JARs above into your local repository:

```shell
% mvn install:install-file -Dfile=/path/to/variant-java-client-<release>.jar -DgroupId=com.variant -DartifactId=java-client -Dversion=<release> -Dpackaging=jar

% mvn install:install-file -Dfile=/path/to/variant-core-<release>.jar -DgroupId=com.variant -DartifactId=variant-core -Dversion=<release> -Dpackaging=jar
```

Replace &lt;release&gt; with the particular version number you're installing, e.g. "0.7.1".

Variant Java client has a small set of external transitive dependencies, which are not included in the distribution:

### 2.1 Build the Servlet Adapter
```shell
% mvn clean install
```

Updated on 19 July 2017 for release 1.0.0.
