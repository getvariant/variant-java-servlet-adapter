package com.variant.client.servlet;

import javax.servlet.http.HttpServletResponse;

import com.variant.client.StateRequest;

/**
 * <p>Servlet-aware wrapper around bare client's implementation of {@link StateRequest}.
 * Overrides the {@link StateRequest#commit(Object...)} method with the
 * Servlet-aware {@link #commit(HttpServletResponse)}. 
 * 
 * @author Igor Urisman
 * @since 0.7
 */

public interface ServletStateRequest extends StateRequest {
	
	/**
	 * Environment bound version of of the Java client's {@link #commit(Object...)}.
	 * 
	 * @param request Current {@link HttpServletResponse}.
	 * @since 0.7
	 */
	void commit(HttpServletResponse response);
	
	/**
	 * Environment bound version of of the Java client's {@link #fail(Object...)}.
	 * 
	 * @param request Current {@link HttpServletResponse}.
	 * @since 0.9
	 */
	void fail(HttpServletResponse response);

}
