package com.variant.client.servlet;

import com.variant.client.Connection;
import com.variant.client.SessionIdTracker;
import com.variant.client.TargetingTracker;
import com.variant.client.UnknownSchemaException;
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
 *  * 
 * @see VariantClient
 * 
 * @since 0.7
 */
public interface ServletVariantClient extends VariantClient {
	
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
	ServletConnection connectTo(String uri);

	/**
	 * Override the bare {@link VariantClient.Builder} to fix the return types.
	 * 
	 * @see VariantClient.Builder
	 * @since 0.10
	 */
	public static class Builder extends VariantClient.Builder {		

		/**
		 * Default builder uses {@link SessionIdTrackerHttpCookie} and {@link TargetingTrackerHttpCookie}.
		 * 
		 * @since 0.10
		 */
		public Builder() {
			withSessionIdTrackerClass(SessionIdTrackerHttpCookie.class);
			withTargetingTrackerClass(TargetingTrackerHttpCookie.class);
		}
		
		/**
		 * @see {@link VariantClient.Builder#build()}
		 * @since 0.10
		 */
		@Override
		public Builder withTargetingStabilityDays(int days) {
			return (Builder) super.withTargetingStabilityDays(days);
		}
		
		/**
		 * @see {@link VariantClient.Builder#build()}
		 * @since 0.10
		 */
		@Override
		public Builder withTargetingTrackerClass(Class<? extends TargetingTracker> klass) {
			return (Builder) super.withTargetingTrackerClass(klass);
		}
		
		/**
		 * @see {@link VariantClient.Builder#build()}
		 * @since 0.10
		 */
		public Builder withSessionIdTrackerClass(Class<? extends SessionIdTracker> klass) {
			return (Builder) super.withSessionIdTrackerClass(klass);
		}

		/**
		 * @see {@link VariantClient.Builder#build()}
		 * @since 0.10
		 */
		public ServletVariantClient build() {
			
			return new ServletClientImpl(super.build());
		}
	}
		
}
