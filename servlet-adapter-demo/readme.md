![Variant Logo](http://www.getvariant.com/wp-content/uploads/2016/07/VariantLogoSquare-100.png)

# Variant Demo Application
### Release 1.0.0
#### Requires: Java 7 or later, Java Servlet API 2.4 or later, Variant Client 0.7 + Variant Servlet Adapter

[__Documentation__](http://www.getvariant.com/docs/0-7/installation-and-demo/#section-3)

__Variant demo application__ is built with the popular [Pet Clinic webapp](https://github.com/spring-projects/spring-petclinic), available from the Sprinig MVC Web framework. This doesn't mean that your application must also use Spring MVC — we're using it for demonstration purposes only, as it provides the opportunity to demonstrate both Variant's [bare Java client](http://getvariant.com/docs/0-7/clients/variant-java-client/#section-2) as well as the [servlet adapter](/servlet-adapter).

## 1. Installation

∎ Clone this repository to your local system:
```
% git clone https://github.com/getvariant/variant-java-servlet-adapter.git
```
Out-of-the-box, the demo application looks for Variant server at the default URL `http://localhost:5377/variant`. If your server is running elsewhere, you must update the `server.url` property in the Variant client configuration file [variant-java-servlet-adapter/servlet-adapter-demo/src/main/resources/variant.conf](https://github.com/getvariant/variant-java-servlet-adapter/blob/master/servlet-adapter-demo/src/main/resources/variant.conf).

∎ Change to the demo application directory:
```
% cd servlet-adapter-demo
```
∎ start the demo application:
```
% mvn tomcat7:run
```

The demo application is accessible at <span class="variant-code">http://localhost:9966/petclinic/</span>.

## 2. Running

∎ If you haven't done so yet, [download and install](http://www.getvariant.com/docs/0-7/experiment-server/reference/#section-1) Variant Experiment Server.

∎ Start Variant server:
```
% /path/to/server/bin/variant.sh start
```

## 3 Running the Demo Experiment

The demo experiment is instrumented on the `New Owner` page. You navigate to it from the home page by clicking "Find Owners", followed by "New Owner". The original page, that the demo application comes with, looks like this:

<a href="http://www.getvariant.com/wp-content/uploads/2015/11/outOfTheBox.png"><img class="alignnone wp-image-519 size-large" src="http://www.getvariant.com/wp-content/uploads/2015/11/outOfTheBox-1024x892.png" alt="outOfTheBox" width="610" height="531" /></a>

The demo experiment introduces two new variations of this page: <span class="variant-code">tosCheckBox</span> and <span class="variant-code">tos&amp;mailCheckbox</span> as illustrated below.

<img class="alignnone size-large wp-image-517" src="http://www.getvariant.com/wp-content/uploads/2015/11/tosCheckbox-1024x954.png" alt="tosCheckbox" width="610" height="568" />

The <span class="variant-code">tosCheckBox</span> variant adds to the control experience the ToS (terms of service) check box, presumably, on insistence of the legal department.

<img class="alignnone size-large wp-image-518" src="http://www.getvariant.com/wp-content/uploads/2015/11/tosmailCheckbox-1024x1000.png" alt="tos&amp;mailCheckbox" width="610" height="596" />

The <span class="variant-code">tos&amp;mailCheckbox</span> variant adds the email list opt-in check box in addition to the ToS checkbox. 

The metric we're after in this experiment is the next page conversion rate, i.e. the ratio of visitors who completed the form and ended up on the Owner Information page to those who came to the New Owner page. 

In order to demonstrate the power of <a href="/docs/0-7/experiment-server/server-user-guide/#section-8">Variant Server's Extension API</a>, the demo application is configured with two user hooks: <span class="variant-code"><a href="https://github.com/getvariant/variant-server-extapi/blob/master/server-extapi-demo/src/main/java/com/variant/server/ext/demo/FirefoxDisqualHook.java" target="_blank">FirefoxDisqualifier&nbsp;<i class="fa fa-external-link"></i></a></span> and <span class="variant-code"><a href="https://github.com/getvariant/variant-server-extapi/blob/master/server-extapi-demo/src/main/java/com/variant/server/ext/demo/ChromeTargetingHook.java" target="_blank">ChromeTargeter&nbsp;<i class="fa fa-external-link"></i></a></span>. The former disqualifies all traffic coming from a Firefox browser, and the latter targets all traffic coming from a Chrome browser to the control experience. 

If you visit the Petclinic site using a Firefox browser, your experience will be equivalent to there being no experiment at all: you will always see the existing experience and your visit to the New Owner page will not trigger Variant events. Similarly, if you use a Chrome browser, you will always see the existing experience. However, when you touch instrumented pages Variant will generate and log experiment related events. 

Although this behavior may seem contrived, it demonstrates how easy it is to inject experiment qualification or targeting semantics into your Variant server.

If you navigate to the New Owner page in any other browser, you may land on either of the three variants. Once there, you will notice the <span class="variant-code">STATE-VISIT</span> event in the server log after a short delay due to the asynchronous nature of the server event writer:

[crayon]
[info] c.v.s.e.EventFlusherAppLogger - {event_name:'$STATE_VISIT', created_on:'1500325855912', event_value:'newOwner', session_id:'55D817210864DB27', event_experiences:[{test_name:'NewOwnerTest', experience_name:'tosCheckbox', is_control:false}], event_params:[{key:'PATH', value:'/owners/new/variant/newOwnerTest.tosCheckbox'}, {key:'$REQ_STATUS', value:'OK'}]}
[info] c.v.s.e.EventWriter$FlusherThread - Flushed 1 event(s) in 00:00:00.002
[/crayon]

The <span class="variant-code">STATE-VISIT</span> event is automatically generated by Variant each time a user session requests a state&mdash;which is to say a web page, in the case of traditional Web applications,&mdash;that is instrumented by a live Variant test. User code has access to these events and can add application specific parameters which may aid at experiment analysis time.

Out-of-the-box, Variant server's event writer is configured to flush event to the server log file. Likely, you will opt for an event flusher that writes events to some more manageable persistent storage. Variant Server comes with event flushers for H2 and PostgreSQL relational databases, which you can <a href="/docs/0-7/experiment-server/reference/#section-3">configure to match your environment</a>. It is also easy to configure Variant server to use a custom event flusher.

If you happen to return to the New Owner page, you will always see the same experience &mdash; the feature known as targeting stability. The Pet Clinic demo application comes pre-configured with the HTTP cookie based implementation. If you want to let Variant re-target your session, simply remove both <span class="variant-code">variant-target</span> and <span class="variant-code">variant-ssnid</span> cookies from your browser.

<a name="section-5" class="variant-header-offset"></a>
<h1><span class="variant-underlined">5<span class="indent"></span>Discussion</span></h1>

In this section we describe in detail the steps involved in creating and executing the demo test you just ran in the previous section.

<a name="section-5.1" class="variant-header-offset"></a>
<h2><span class="variant-underlined">5.1. Experience Implementation</span></h2>

The very first task in creating a new online experiment is to implement the variant experiences that will be compared to the existing code path. To accomplish this, we did the following:

<span class="tombstone">∎</span> Created controller mappings in the class <span class="variant-code"><a href="https://github.com/getvariant/variant-java-servlet-adapter/blob/master/servlet-adapter-demo/src/main/java/org/springframework/samples/petclinic/web/OwnerController.java#L79-L117" target="_blank">OwnerController.java&nbsp;<i class="fa fa-external-link"></i></a></span> for the new resource paths <span class="variant-code">/owners/new/variant/newOwnerTest.tosCheckbox</span> and <span class="variant-code">/owners/new/variant/newOwnerTest.tosAndMailCheckbox</span> &mdash; the entry points into the new experiences. Whenever Variant targets a session for a non-control variant of the <span class="variant-code">newOwner</span> page, it will forward the current HTTP request to that path. Otherwise, the request will proceed to the control page at the originally requested path <span class="variant-code">/owners/new/</span>.

<span class="tombstone">∎</span> Created two new JSP pages <span class="variant-code"><a href="https://github.com/getvariant/variant-java-servlet-adapter/blob/master/servlet-adapter-demo/src/main/webapp/WEB-INF/jsp/owners/createOrUpdateOwnerForm__newOwnerTest.tosCheckbox.jsp" target="_blank">createOrUpdateOwnerForm__newOwnerTest.tosCheckbox.jsp&nbsp;<i class="fa fa-external-link"></i></a></span> and <span class="variant-code"><a href="https://github.com/getvariant/variant-java-servlet-adapter/blob/master/servlet-adapter-demo/src/main/webapp/WEB-INF/jsp/owners/createOrUpdateOwnerForm__newOwnerTest.tosAndMailCheckbox.jsp"  target="_blank">createOrUpdateOwnerForm__newOwnerTest.tosAndMailCheckbox.jsp&nbsp;<i class="fa fa-external-link"></i></a></span>, implementing the two new variants of the new owner page.

<a name="section-5.2" class="variant-header-offset"></a>
<h2><span class="variant-underlined">5.2. Experiment Instrumentation</span></h2>

<span class="tombstone">∎</span> The first step in instrumenting any Variant experiment is to develop its experiment schema. Variant server comes with the schema <span class="variant-code">petclnic-schema.json</span> already in the <span class="variant-code">/schemata</span> directory:
[crayon lang="javascript" line="1"]
//
// Variant Java client + Servlet adapter demo application.
// Demonstrates instrumentation of a basic Variant experiment.
// See https://github.com/getvariant/variant-java-servlet-adapter/tree/master/servlet-adapter-demo
// for details.
//
// Copyright © 2015-2016 Variant, Inc. All Rights Reserved.

{
   'meta':{
      'name':'petclinic',
      'comment':'Experiment schema for the Pet Clinic demo application'
   },
   'states':[                                                
     { 
       // The New Owner page used to add owner at /petclinic/owners/new 
       'name':'newOwner',                                     
       'parameters': {
           'path':'/petclinic/owners/new'
        }                                                    
     },                                                    
     {  
       // The Owner Detail page. Note that owner ID is in the path,
        // so we have to use regular expression to match.
       'name':'ownerDetail',          
       'parameters': {
           'path':'/petclinic/owners/~\\d+/'
        }                                                    
     }                                                     
   ],                                                        
   'tests':[                                                 
      {                                                      
         'name':'NewOwnerTest',
         'isOn': true,                                     
         'experiences':[                                     
            {                                                
               'name':'outOfTheBox',                                   
               'weight':1,                                  
               'isControl':true                              
            },                                               
            {                                                
               'name':'tosCheckbox',                                   
               'weight':1                                   
            },                                               
            {                                                
               'name':'tosAndMailCheckbox',                                   
               'weight':1                                   
            }                                               
         ],                                                  
         'onStates':[                                         
            {                                                
               'stateRef':'newOwner',                    
               'variants':[                                  
                  {                                          
                     'experienceRef': 'tosCheckbox',                   
                   'parameters': {
                        'path':'/owners/new/variant/newOwnerTest.tosCheckbox'         
                     }                                          
                  },                                         
                  {                                          
                     'experienceRef': 'tosAndMailCheckbox',                   
                   'parameters': {
                        'path':'/owners/new/variant/newOwnerTest.tosAndMailCheckbox'         
                     }                                          
                  }                                          
               ]                                             
            },
            {                                                
               'stateRef':'ownerDetail',                            
               'isNonvariant': true
            }                                                
         ],
         'hooks':[
            {
               'name':'FirefoxDisqualifier',
               'class':'com.variant.server.ext.demo.FirefoxDisqualHook'
            },
            {
               'name':'ChromeTargeter',
               'class':'com.variant.server.ext.demo.ChromeTargetingHook'
            }
         ]
      }                                                     
   ]                                                         
}                                                  
[/crayon]

The two states (lines 14-30) correspond to the two consecutive pages in the experiment: <span class="variant-code">newOwner</span> and <span class="variant-code">ownerDetail</span>. The sole experiment <span class="variant-code">NewOwnerTest</span> has three experiences (lines 35-49) with equal weights, i.e. roughly equal number of users sessions will be targeted to each of the experiences. The test is instrumented on both pages, although the <span class="variant-code">ownerDetail</span> page is defined as non-variant (lines 68-71), which means that visitors will see the same page regardless of the targeted experience. The <span class="variant-code">newOwner</span> page, however, has two variants: one for each non-control experience (lines 53-66).

<span class="tombstone">∎</span> Created <span class="variant-code"><a href="https://github.com/getvariant/variant-java-servlet-adapter/blob/master/servlet-adapter-demo/src/main/java/com/variant/client/servlet/demo/PetclinicVariantFilter.java" target="_blank">PetclinicVariantFilter&nbsp;<i class="fa fa-external-link"></i></a></span> and configured it in the Petclinic applciation's <span class="variant-code"><a href="https://github.com/getvariant/variant-java-servlet-adapter/blob/master/servlet-adapter-demo/src/main/webapp/WEB-INF/web.xml#L124-L137" target="_blank">web.xml&nbsp;<i class="fa fa-external-link"></i></a></span> file. In general, servlet filter is the instrumentation mechanism behind the <a href="/docs/0-7/clients/variant-java-client/#section-3">servlet adapter</a>. Here, we extend the base <span class="variant-code"><a href="https://github.com/getvariant/variant-java-servlet-adapter/blob/master/servlet-adapter/src/main/java/com/variant/client/servlet/VariantFilter.java" target="_blank">VariantFilter&nbsp;<i class="fa fa-external-link"></i></a></span> with additional semantics: whenever the base <span class="variant-code">VariantFilter</span> obtains a foreground session, the User-Agent header from the incoming request is saved as a session attribute. This will be used by the server side user hooks in order to disqualify or target user sessions based on what Web browser they are coming from.

<span class="tombstone">∎</span> Created <span class="variant-code"><a href="https://github.com/getvariant/variant-server-extensions/blob/master/server-extensions-demo/src/main/java/com/variant/server/ext/demo/FirefoxDisqualHook.java" target="_blank">FirefoxDisqualifier&nbsp;<i class="fa fa-external-link"></i></a></span> and <span class="variant-code"><a href="https://github.com/getvariant/variant-server-extensions/blob/master/server-extensions-demo/src/main/java/com/variant/server/ext/demo/FirefoxDisqualHook.java" target="_blank">ChromeTargeter&nbsp;<i class="fa fa-external-link"></i></a></span> user hooks. See TODO for details on how to develop for the server extensions API. Out-of-the-box, Variant server comes with the <span class="variant-code">/ext/server-extensions-demo-0.7.1.jar</span> JAR file, containing class files for these hooks.

<span class="tombstone">∎</span> Instrumented the submit button on the <span class="variant-code">newOwner</span> page to send a custom <span class="variant-code">CLICK</span> event to the server when the button is pressed by editing the <span class="variant-code"><a href="https://github.com/getvariant/variant-java-servlet-adapter/blob/master/servlet-adapter-demo/src/main/webapp/WEB-INF/jsp/fragments/staticFiles.jsp#L32-L68" target="_blank">staticFiles.jsp&nbsp;<i class="fa fa-external-link"></i></a></span> file.




Updated on 19 July 2017.
