![Variant Logo](http://www.getvariant.com/wp-content/uploads/2016/07/VariantLogoSquare-100.png)

# Variant Demo Application
### Release 1.0.0
#### Requires: Java 7 or later, Java Servlet API 2.4 or later, Maven

__Variant demo application__ is built with the popular [Pet Clinic webapp](https://github.com/spring-projects/spring-petclinic), available from the Sprinig MVC Web framework. This doesn't mean that your application must also use Spring MVC — we're using it for demonstration purposes only, as it provides the opportunity to demonstrate both Variant's [bare Java client](http://getvariant.com/docs/0-7/clients/variant-java-client/#section-2) as well as the [servlet adapter](/servlet-adapter).

## 1. Start Variant Server

1. If you haven't done so yet, [download and install](http://www.getvariant.com/docs/0-7/experiment-server/reference/#section-1) Variant Experiment Server.

2. Start Variant server:
```
% /path/to/server/bin/variant.sh start
```
Note, that Variant server comes pre-configured to run the demo application out-of-the-box. The `/schemata` directory contains the demo experiment schema file `petclinic-schema.json`, and the `/ext` directory contains the `server-extensions-demo-<release>.jar` file, containing the user hooks, required to run the Pet Clinic demo application.

If all went well, the server console output should look something like this:
```
[info] c.v.c.u.VariantConfigLoader - Found config resource [/variant.conf] as [/private/tmp/demo/variant-server-0.7.1/conf/variant.conf]
[info] c.v.s.s.SchemaDeployerFromFS - Deployed schema [petclinic], ID [1CC06C031F2D3F85]:
   NewOwnerTest:[outOfTheBox (control), tosCheckbox, tosAndMailCheckbox] (ON)
[info] c.v.s.b.VariantServerImpl - Variant Experiment Server release 0.7.1 bootstrapped on :5377/variant in 00:00.084.[/crayon]
```

## 2. Deploy the Demo Appliction

1. Clone This Repository:
```
% git clone https://github.com/getvariant/variant-java-servlet-adapter.git
```
2. Install Maven dependencies

Variant Demo application is built on top of the [servlet adapter](/servlet-adapter). It is included in this repository's `/lib` directory and must be installed in your local Maven repository. Assuming `/lib` to be your current directory:

```
% mvn install:install-file -Dfile=variant-java-client-<release>.jar -DgroupId=com.variant -DartifactId=java-client -Dversion=<release> -Dpackaging=jar

% mvn install:install-file -Dfile=variant-core-<release>.jar -DgroupId=com.variant -DartifactId=variant-core -Dversion=<release> -Dpackaging=jar

% mvn install:install-file -Dfile=java-client-servlet-adapter-1.0.0.jar -DgroupId=com.variant -DartifactId=java-client-servlet-adapter -Dversion=1.0.0 -Dpackaging=jar
```

3. Deploy the Demo Application

Out-of-the-box, the demo application looks for Variant server at the default URL `http://localhost:5377/variant`. If your server is running elsewhere, you must update the `server.url` property in the Variant client configuration file [variant-java-servlet-adapter/servlet-adapter-demo/src/main/resources/variant.conf](https://github.com/getvariant/variant-java-servlet-adapter/blob/master/servlet-adapter-demo/src/main/resources/variant.conf).

4. Change to the demo application directory:
```
% cd servlet-adapter-demo
```
5. Start the demo application:
```
% mvn tomcat7:run
```

If all went well, you will see the following console output:
```
INFO  2017-08-03 16:46:42 VariantConfigLoader - Found config resource [/variant.conf] as [/private/tmp/demo/variant-java-servlet-adapter/servlet-adapter-demo/target/classes/variant.conf]
INFO  2017-08-03 16:46:43 VariantFilter - Connected to schema [petclinic]
```
The demo application is accessible at <span class="variant-code">http://localhost:9966/petclinic/</span>.

## 3. Run the Demo Experiment

The demo experiment is instrumented on the `New Owner` page. You navigate to it from the home page by clicking "Find Owners", followed by "New Owner". The original page, that the demo application comes with, looks like this:

| <img class="alignnone wp-image-519 size-large" src="http://www.getvariant.com/wp-content/uploads/2015/11/outOfTheBox-1024x892.png" alt="outOfTheBox" width="610" height="531" /> |
| ------------- |
| __Fig. 1. The orignal New Owner page.__ | 

<br>

The demo experiment introduces two new variations of this page called `tosCheckBox` and `tos&mailCheckbox`, as illustrated below.

| <img class="alignnone size-large wp-image-517" src="http://www.getvariant.com/wp-content/uploads/2015/11/tosCheckbox-1024x954.png" alt="tosCheckbox" width="610" height="568" /> | 
| ------------- | 
| __Fig. 2. The `tosCheckBox` variant adds to the control experience the ToS (terms of service) check box, presumably, on insistence of the legal department.__ |

<br>

| <img class="alignnone size-large wp-image-518" src="http://www.getvariant.com/wp-content/uploads/2015/11/tosmailCheckbox-1024x1000.png" alt="tos&mailCheckbox" width="610" height="596" /> | 
| ------------- |
| __Fig. 3. The `tos&mailCheckbox` variant adds the email list opt-in check box in addition to the ToS checkbox.__ | 
 
<br>

The metric we're after in this experiment is the next page conversion rate, i.e. the ratio of visitors who completed the form and ended up on the Owner Information page to those who came to the New Owner page. 

In order to demonstrate the power of [Variant Server's Extension API](http://getvariant.com/docs/0-7/experiment-server/server-user-guide/#section-8), the demo application is configured with two user hooks: [FirefoxDisqualifier](https://github.com/getvariant/variant-server-extapi/blob/master/server-extapi-demo/src/main/java/com/variant/server/ext/demo/FirefoxDisqualHook.java) and [ChromeTargeter](https://github.com/getvariant/variant-server-extapi/blob/master/server-extapi-demo/src/main/java/com/variant/server/ext/demo/ChromeTargetingHook.java). The former disqualifies all traffic coming from a Firefox browser, and the latter targets all traffic coming from a Chrome browser to the control experience. 

If you visit the Petclinic site using a Firefox browser, your experience will be equivalent to there being no experiment at all: you will always see the existing experience and your visit to the New Owner page will not trigger Variant events. Similarly, if you use a Chrome browser, you will always see the existing experience, but when you touch instrumented pages Variant will generate and log experiment related events. Although this behavior may seem contrived, it demonstrates how easy it is to inject experiment qualification or targeting semantics into your Variant server.

If you navigate to the New Owner page in any other browser, you may land on either of the three variants. Once there, you will notice the `STATE-VISIT` event in the server log, after a short delay due to the asynchronous nature of the server event writer:

```
[info] c.v.s.e.EventFlusherAppLogger - {event_name:'$STATE_VISIT', created_on:'1500325855912', event_value:'newOwner', session_id:'55D817210864DB27', event_experiences:[{test_name:'NewOwnerTest', experience_name:'tosCheckbox', is_control:false}], event_params:[{key:'PATH', value:'/owners/new/variant/newOwnerTest.tosCheckbox'}, {key:'$REQ_STATUS', value:'OK'}]}
[info] c.v.s.e.EventWriter$FlusherThread - Flushed 1 event(s) in 00:00:00.002
```

`STATE-VISIT` event is automatically generated by Variant each time a user session requests a state—which is to say a web page, in the case of a traditional Web application,—that is instrumented by a live Variant test. User code has access to these events and can add application specific parameters which may aid at experiment analysis time.

Out-of-the-box, Variant server's event writer is configured to flush event to the server log file. Likely, you will opt for an event flusher that writes events to some more manageable persistent storage. Variant Server comes with event flushers for H2 and PostgreSQL relational databases, which you can [configure to match your environment](http://getvariant.com/docs/0-7/experiment-server/reference/#section-4.3). It is also easy to configure Variant server to use a custom event flusher.

If you happen to return to the New Owner page, you will always see the same experience &mdash; the feature known as targeting stability. The Pet Clinic demo application comes pre-configured with the HTTP cookie based implementation. If you want to let Variant re-target your session, simply remove both `variant-target` and `variant-ssnid` cookies from your browser.

<a name="section-5" class="variant-header-offset"></a>
<h1><span class="variant-underlined">5<span class="indent"></span>Discussion</span></h1>

In this section we describe in detail the steps involved in creating and executing the demo test you just ran in the previous section.

## 4. Discussion

The very first task in creating a new online experiment is to implement the variant experiences that will be compared to the existing code path. To accomplish this, we did the following:

1. Created controller mappings in the class <a href="https://github.com/getvariant/variant-java-servlet-adapter/blob/master/servlet-adapter-demo/src/main/java/org/springframework/samples/petclinic/web/OwnerController.java#L79-L117" target="_blank">OwnerController.java</a> for the new resource paths `/owners/new/variant/newOwnerTest.tosCheckbox` and `/owners/new/variant/newOwnerTest.tosAndMailCheckbox` — the entry points into the new experiences. Whenever Variant targets a session for a non-control variant of the `newOwner` page, it will forward the current HTTP request to that path. Otherwise, the request will proceed to the control page at the originally requested path `/owners/new/`.

2. Created two new JSP pages <a href="https://github.com/getvariant/variant-java-servlet-adapter/blob/master/servlet-adapter-demo/src/main/webapp/WEB-INF/jsp/owners/createOrUpdateOwnerForm__newOwnerTest.tosCheckbox.jsp" target="_blank">createOrUpdateOwnerForm__newOwnerTest.tosCheckbox.jsp</a> and <a href="https://github.com/getvariant/variant-java-servlet-adapter/blob/master/servlet-adapter-demo/src/main/webapp/WEB-INF/jsp/owners/createOrUpdateOwnerForm__newOwnerTest.tosAndMailCheckbox.jsp"  target="_blank">createOrUpdateOwnerForm__newOwnerTest.tosAndMailCheckbox.jsp</a>, implementing the two new variants of the new owner page.


3. Created the experiment schema. 
```
//
// Variant Java client + Servlet adapter demo application.
// Demonstrates instrumentation of a basic Variant experiment.
// See https://github.com/getvariant/variant-java-servlet-adapter/tree/master/servlet-adapter-demo
// for details.
//
// Copyright © 2015-2016 Variant, Inc. All Rights Reserved.

{
   'meta':{                                                                                 // 10
      'name':'petclinic',
      'comment':'Experiment schema for the Pet Clinic demo application'
   },
   'states':[                                                
     { 
       // The New Owner page used to add owner at /petclinic/owners/new 
       'name':'newOwner',                                     
       'parameters': {
           'path':'/petclinic/owners/new'
        }                                                                                   // 20
     },                                                    
     {  
       // The Owner Detail page. Note that owner ID is in the path,
        // so we have to use regular expression to match.
       'name':'ownerDetail',          
       'parameters': {
           'path':'/petclinic/owners/~\\d+/'
        }                                                    
     }                                                     
   ],                                                                                       // 30
   'tests':[                                                 
      {                                                      
         'name':'NewOwnerTest',
         'isOn': true,                                     
         'experiences':[                                     
            {                                                
               'name':'outOfTheBox',                                   
               'weight':1,                                  
               'isControl':true                              
            },                                                                              // 40
            {                                                
               'name':'tosCheckbox',                                   
               'weight':1                                   
            },                                               
            {                                                
               'name':'tosAndMailCheckbox',                                   
               'weight':1                                   
            }                                               
         ],                                                  
         'onStates':[                                                                       // 50
            {                                                
               'stateRef':'newOwner',                    
               'variants':[                                  
                  {                                          
                     'experienceRef': 'tosCheckbox',                   
                   'parameters': {
                        'path':'/owners/new/variant/newOwnerTest.tosCheckbox'         
                     }                                          
                  },                                         
                  {                                                                         // 60
                     'experienceRef': 'tosAndMailCheckbox',                   
                   'parameters': {
                        'path':'/owners/new/variant/newOwnerTest.tosAndMailCheckbox'         
                     }                                          
                  }                                          
               ]                                             
            },
            {                                                
               'stateRef':'ownerDetail',                            
               'isNonvariant': true                                                         // 70
            }                                                
         ],
         'hooks':[
            {
               'name':'FirefoxDisqualifier',
               'class':'com.variant.server.ext.demo.FirefoxDisqualHook'
            },
            {
               'name':'ChromeTargeter',
               'class':'com.variant.server.ext.demo.ChromeTargetingHook'                    // 80
            }
         ]
      }                                                     
   ]                                                         
}                                                  
```
Note, that Variant server comes out-of-the-boxwith this schema already in the `schemata` directory.

The two states (lines 14-30) correspond to the two consecutive pages in the experiment: <span class="variant-code">newOwner</span> and <span class="variant-code">ownerDetail</span>. The sole experiment <span class="variant-code">NewOwnerTest</span> has three experiences (lines 35-49) with equal weights, i.e. roughly equal number of users sessions will be targeted to each of the experiences. The test is instrumented on both pages, although the <span class="variant-code">ownerDetail</span> page is defined as non-variant (lines 68-71), which means that visitors will see the same page regardless of the targeted experience. The <span class="variant-code">newOwner</span> page, however, has two variants: one for each non-control experience (lines 53-66).

<span class="tombstone">∎</span> Created <span class="variant-code"><a href="https://github.com/getvariant/variant-java-servlet-adapter/blob/master/servlet-adapter-demo/src/main/java/com/variant/client/servlet/demo/PetclinicVariantFilter.java" target="_blank">PetclinicVariantFilter&nbsp;<i class="fa fa-external-link"></i></a></span> and configured it in the Petclinic applciation's <span class="variant-code"><a href="https://github.com/getvariant/variant-java-servlet-adapter/blob/master/servlet-adapter-demo/src/main/webapp/WEB-INF/web.xml#L124-L137" target="_blank">web.xml&nbsp;<i class="fa fa-external-link"></i></a></span> file. In general, servlet filter is the instrumentation mechanism behind the <a href="/docs/0-7/clients/variant-java-client/#section-3">servlet adapter</a>. Here, we extend the base <span class="variant-code"><a href="https://github.com/getvariant/variant-java-servlet-adapter/blob/master/servlet-adapter/src/main/java/com/variant/client/servlet/VariantFilter.java" target="_blank">VariantFilter&nbsp;<i class="fa fa-external-link"></i></a></span> with additional semantics: whenever the base <span class="variant-code">VariantFilter</span> obtains a foreground session, the User-Agent header from the incoming request is saved as a session attribute. This will be used by the server side user hooks in order to disqualify or target user sessions based on what Web browser they are coming from.

<span class="tombstone">∎</span> Created <span class="variant-code"><a href="https://github.com/getvariant/variant-server-extensions/blob/master/server-extensions-demo/src/main/java/com/variant/server/ext/demo/FirefoxDisqualHook.java" target="_blank">FirefoxDisqualifier&nbsp;<i class="fa fa-external-link"></i></a></span> and <span class="variant-code"><a href="https://github.com/getvariant/variant-server-extensions/blob/master/server-extensions-demo/src/main/java/com/variant/server/ext/demo/FirefoxDisqualHook.java" target="_blank">ChromeTargeter&nbsp;<i class="fa fa-external-link"></i></a></span> user hooks. See TODO for details on how to develop for the server extensions API. Out-of-the-box, Variant server comes with the <span class="variant-code">/ext/server-extensions-demo-0.7.1.jar</span> JAR file, containing class files for these hooks.

<span class="tombstone">∎</span> Instrumented the submit button on the <span class="variant-code">newOwner</span> page to send a custom <span class="variant-code">CLICK</span> event to the server when the button is pressed by editing the <span class="variant-code"><a href="https://github.com/getvariant/variant-java-servlet-adapter/blob/master/servlet-adapter-demo/src/main/webapp/WEB-INF/jsp/fragments/staticFiles.jsp#L32-L68" target="_blank">staticFiles.jsp&nbsp;<i class="fa fa-external-link"></i></a></span> file.




Updated on 19 July 2017.
