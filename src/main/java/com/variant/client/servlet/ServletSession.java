package com.variant.client.servlet;

import com.variant.client.Session;
import com.variant.share.schema.State;

/**
 * <p>Servlet-aware wrapper around bare client's implementation of {@link Session}.
 * Overrides the {@link Session#targetForState(State)} method to return the narrower,
 * Servlet-aware state request object {@link ServletStateRequest}. 
 * 
 * @author Igor Urisman
 * @since 0.5
 */
public interface ServletSession extends Session {

	/**
	 * Override the underlying {@link Session#targetForState(State)} in order to
	 * return the narrower servlet-aware state request {@link ServletStateRequest}
	 * @since 0.6
	 */
	@Override
	public ServletStateRequest targetForState(State state);

}
