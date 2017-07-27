# Servlet Adapter for Variant Java Client 1.0.0
## Wrapper API around Variant Java Client suitable for host application written on top of the Servlet API.
#### Requires: Java 7 or later, Java Servlet API 2.4 or later, Variant Client 0.7

Most Java Web applications are written on top of the Servlet API, either directly or via a servlet-based framework, such as Struts, Spring or Lift. Such applications, instead of coding directly to Variant Java client, should take advantage of this servlet adapter. 

The servlet adapter wraps the bare Java client with a higher level client library, which re-writes environment-dependent function signatures in terms of familiar servlet objects, like <span class="variant-code">HttpServletRequest</span>. The servlet adapter preserves 100% of the bare client’s functionality and comes with out-of-the-box implementations of all environment-dependent classes.

Variant servlet adapter for the Java client contains the following three components:
* [VariantFilter](https://getvariant.github.io/variant-java-servlet-adapter/com/variant/client/servlet/VariantFilter.html) bootstraps the underlying Variant client and implements all the core functionality a simple Variant experiment will require. Integrates with the host application as a servlet filter.
* Implementations of all environment-dependent in terms of servlet API objects. 
* [Servlet adapter wrapper API](https://github.com/getvariant/variant-java-servlet-adapter/tree/master/servlet-adapter) around the bare Java client replaces environment contingent method signatures with equivalent ones expressed in terms of the Servlet API classes.

[Documentation](http://www.getvariant.com/docs/0-7/clients/variant-java-client/#section-3) | [Javadoc](https://getvariant.github.io/variant-java-servlet-adapter/)
Updated on 19 July 2017 for release 1.0.0.
