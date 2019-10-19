package com.variant.client.servlet;

import com.variant.client.Connection;
import com.variant.client.UnknownSchemaException;
import com.variant.client.VariantClient;
import com.variant.client.servlet.impl.ServletConnectionImpl;

/**
 * <p>Servlet Adapter for the "bare" Variant Java Client, providing greatly simplified integration
 * for host applications written on top of the Java Servlet API.
 * 
 * <p>A wrapper API around the "bare" Java Client. Replaces the environment-dependent method signatures
 * with their environment-aware counterparts, e.g. 
 * {@link com.variant.client.servlet.ServletConnection#getSession(javax.servlet.http.HttpServletRequest)}
 * in place of the bare {@link com.variant.client.Connection#getSession(Object...)}.
 *  * 
 * @see VariantClient
 * 
 * @since 0.7
 */
public class ServletVariantClient implements VariantClient {
		   
   /**
    * Factory method for obtaining a new instance of {@link ServletVariantClient}.
    * Host application should hold on to and reuse the object returned by this method whenever possible.
    * In most cases, one {@link VariantClient} instance per application is sufficient.
    * 
    * @return Instance of the {@link ServerVariantClient} type. Cannot be null.
    * @since 0.10
    */
   public static ServletVariantClient build() { 
      
      VariantClient bareClient = new VariantClient.Builder()
    		  .withSessionIdTrackerClass(SessionIdTrackerHttpCookie.class)
    		  .withTargetingTrackerClass(TargetingTrackerHttpCookie.class)
    		  .build();

      return new ServletVariantClient(bareClient);
   }
	
	private final VariantClient bareClient;
	
	/**
	 * No public instantiation
	 */
	private ServletVariantClient (VariantClient bareClient) {
		this.bareClient = bareClient;
	}

	/**
	 * Connect to a variation schema on a Variant server by its URI.
	 * Variant schema URI has the following format:
	 * [variant:]//netloc[:port]/schema
	 * 
	 * @param uri The Variant URI to the schema.
	 *        
	 * @return An instance of the {@link Connection} type.
	 * 
	 * @throws UnknownSchemaException if given schema does not exist on the server.
	 * @since 0.7
	 */	
	public ServletConnection connectTo(String uri) {
		Connection bareConnection = bareClient.connectTo(uri);
		return new ServletConnectionImpl(this, bareConnection);
	}

}
