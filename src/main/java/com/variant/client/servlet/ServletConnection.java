package com.variant.client.servlet;

import javax.servlet.http.HttpServletRequest;

import com.variant.client.Connection;

/**
 * <p>Servlet-aware wrapper around bare client's implementation of {@link Connection}.
 * Replaces bare client's session creation methods with ones which take an instance
 * of {@link HttpServletRequest} and return the narrower, servlet-aware {@link ServletSession}
 * 
 * @author Igor Urisman
 * @since 1.0
 */

public interface ServletConnection extends Connection {
	
	/**
	 * Equivalent to {@code getOrCreateSession((HttpServletRequest)userData[0])}.
	 */
	@Override
	ServletSession getOrCreateSession(Object... userData);

	/**
	 * Equivalent to {@code getSession((HttpServletRequest)userData[0])}.
	 */
	@Override
	ServletSession getSession(Object... userData);

	@Override
	ServletSession getSessionById(String sessionId);

	// New methods with servlet-aware signatures.
	// Inherited methods will delegate to one of these.

	ServletSession getOrCreateSession(HttpServletRequest req);
	
	ServletSession getSession(HttpServletRequest req);
	
}
