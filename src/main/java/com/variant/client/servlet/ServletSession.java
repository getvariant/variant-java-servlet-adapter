package com.variant.client.servlet;

//import com.variant.client.Session;
import com.variant.core.schema.State;

/**
 * <p>Servlet-aware wrapper around bare client's implementation of {@link Session}.
 * Overrides the {@link Session#targetForState(State)} method to return the narrower,
 * Servlet-aware state request object {@link ServletStateRequest}. 
 * 
 * @author Igor Urisman
 * @since 1.0
 */
public interface ServletSession extends com.variant.client.Session {

	/**
	 * Override the underlying {@link Session#targetForState(State)} in order to
	 * return the narrower servlet-aware state request {@link ServletStateRequest}
	 * @since 0.6
	 */
	@Override
	public ServletStateRequest targetForState(State state);

}
