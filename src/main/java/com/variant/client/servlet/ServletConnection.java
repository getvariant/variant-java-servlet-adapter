package com.variant.client.servlet;

import javax.servlet.http.HttpServletRequest;

import com.variant.client.Connection;

/**
 * <p>Servlet-aware wrapper around bare client's implementation of {@link Connection}.
 * Replaces bare client's session creation methods with ones which take an instance
 * of {@link HttpServletRequest} and return the narrower, servlet-aware {@link ServletSession}
 * 
 * @author Igor Urisman
 * @since 0.5
 */

public interface ServletConnection extends Connection {
	
	/**
	 * Equivalent to {@code getOrCreateSession((HttpServletRequest)userData[0])}.
	 * Use {@link #getOrCreateSession(HttpServletRequest)} instead.
	 */
	@Override
	ServletSession getOrCreateSession(Object... userData);

	/**
	 * Equivalent to {@code getSession((HttpServletRequest)userData[0])}.
	 * Use {@link #getSession(HttpServletRequest)} instead.
	 */
	@Override
	ServletSession getSession(Object... userData);

	/**
	 * Get an existing session by ID.
	 * @see com.variant.client.Connection#getSessionById(String)
	 */
	@Override
	ServletSession getSessionById(String sessionId);

	/**
	 * Rewritten signature in terms of Servlet API. 
	 * @param req
	 * @return
	 */
	ServletSession getOrCreateSession(HttpServletRequest req);
	
	/**
	 * Rewritten signature in terms of Servlet API. 
	 * @param req
	 * @return
	 */
	ServletSession getSession(HttpServletRequest req);
	
}
