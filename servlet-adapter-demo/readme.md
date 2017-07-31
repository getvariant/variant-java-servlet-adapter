# Servlet Adapter for Variant Java Client
## Variant Demo Application
### Release 1.0.0
#### Requires: Java 7 or later, Java Servlet API 2.4 or later, Variant Client 0.7 + Variant Servlet Adapter

[__Variant demo application__](http://www.getvariant.com/docs/0-7/installation-and-demo/#section-3) is built with the popular [Pet Clinic webapp](href="https://github.com/spring-projects/spring-petclinic), available from the Sprinig MVC Web framework. This doesn't mean that your application must also use Spring MVC — we're using it for demonstration purposes only, as it provides the opportunity to demonstrate both Variant's [bare Java client](/docs/0-7/clients/variant-java-client/#section-2) as well as the [servlet adapter](servlet-adapter/readme.md).

∎ Clone the demo application to your local system:
```shell
% git clone https://github.com/getvariant/variant-java-servlet-adapter.git
```

∎ Chane to the demo application directory:
```shell
% cd servlet-adapter-demo
```

∎ With the server already running (see <a href="#section-2.1">Section 2.1 Installing and Running Variant Server</a> for details), start the demo application:
```shell
% mvn tomcat7:run
```

Out-of-the-box, the demo application is looking for Variant server at the URL `http://localhost:5377/variant`. If your server is running elsewhere, you must update the <span class="variant-code">server.url</span> property in the Variant client configuration file `src/main/resources/variant.conf` and restart the demo application.

The demo application is accessible at (http://localhost:9966/petclinic/).
Updated on 19 July 2017.
