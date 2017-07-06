# Java Servlet Adapter for Variant Experiment Server Java client.

Java Servlet Adapter extends [Variant's Bare Java Client](http://www.getvariant.com/docs/0-7/clients/variant-java-client "Variant Java Client")
for use by those Java Web applictions which are written on top of the Servlet API, either via a Servlet based Web framework, 
such as Struts, Spring or Lift, or directly. The servlet adapter enables applictions to communicate with [Variant Experiment Server](http://www.getvariant.com/docs/0-7/experiment-server/server-user-guide/) in order to conduct sophisticated online controlled experiments.


The servlet adapter wraps the bare Java client with a higher level client library, which re-writes environment-dependent function signatures in terms of familiar servlet objects, like HttpServletRequest. The servlet adapter preserves 100% of the bare client’s functionality and comes with out-of-the-box implementations of all environment-dependent classes.

Variant servlet adapter for the Java client contains the following three components:

* [VariantFilter](https://getvariant.github.io/java-servlet-adapter/javadoc/1.0/com/variant/client/servlet/VariantFilter.html) , which bootstraps the underlying Variant client and implements all the core functionality a simple Variant test will require. Cleanly integrates with the host application as a servlet filter.
* Implementations of all environment-dependent in terms of servlet API objects.
* [Wrapper API](https://getvariant.github.io/java-servlet-adapter/javadoc/1.0/com/variant/client/servlet/package-summary.html) around the bare in Java client, which replaces environment contingent method signatures with equivalent ones expressed in terms of the Servlet API classes.

For more information, refer to the [Documentation](http://www.getvariant.com/docs/0-7/clients/variant-java-client "Variant Java Servlet Adapter").
