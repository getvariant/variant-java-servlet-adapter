package com.variant.client.servlet;

import com.variant.client.VariantClient;
import com.variant.client.servlet.impl.ServletClientImpl;

/**
 * <p>Servlet Adapter for the "bare" Variant Java Client, providing greatly simplified integration
 * for host applications written on top of the Java Servlet API.
 * 
 * <p>A wrapper API around the "bare" Java Client. Replaces the environment-dependent method signatures
 * with their environment-aware counterparts, e.g. 
 * {@link com.variant.client.servlet.ServletConnection#getSession(javax.servlet.http.HttpServletRequest)}
 * in place of the bare {@link com.variant.client.Connection#getSession(Object...)}.
 * 
 * @author Igor Urisman
 * 
 * @see VariantClient
 * 
 * @since 1.0
 */
public interface ServletVariantClient extends VariantClient {
	
	/**
	 * @since 1.0
	 */
	@Override
	public ServletConnection getConnection(String schema);

	/**
	 * Factory class: call <code>getInstance()</code> to obtain a new instance of {@link ServletVariantClient}.
	 * @author Igor Urisman
	 * @since 1.0
	 */
	public static class Factory {
		
		private Factory() {}
		
		/**
		 * Obtain a new instance of {@link ServletVariantClient}.
		 * 
		 * Host application should hold on to and reuse the object returned by this method whenever possible.
		 * One of these per address space is recommended.
		 * 
		 * @return Instance of the {@link ServletVariantClient} type.
		 * @since 1.0
		 */		
		public static ServletVariantClient getInstance() {
			return new ServletClientImpl();
		}
	}
}
