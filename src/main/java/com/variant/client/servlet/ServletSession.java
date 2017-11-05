package com.variant.client.servlet;

import com.variant.client.Session;
import com.variant.core.schema.State;

/**
 * <p>Servlet-aware wrapper around bare client's implementation of {@link Session}.
 * Overrides the {@link Session#targetForState(State)} method to return the narrower,
 * Servlet-aware state request object {@link ServletStateRequest}. 
 * 
 * @author Igor Urisman
 * @since 1.0
 */
public interface ServletSession extends Session {

	// Override the bare {@link VariantCoreSession#targetForState(State)} in order to
	// return the servlet-aware state request {@link ServletStateRequest}
	// @since 1.0
	public ServletStateRequest targetForState(State state);

}
