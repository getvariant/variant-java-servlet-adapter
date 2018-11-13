package com.variant.client.servlet.impl;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.variant.client.Connection;
import com.variant.client.Session;
import com.variant.client.servlet.ServletConnection;
import com.variant.client.servlet.ServletSession;
import com.variant.client.servlet.ServletVariantClient;
import com.variant.client.servlet.ServletVariantException;

/**
 * The implementation of {@link ServletConnection}.
 * 
 * @author Igor Urisman
 * @since 1.0
 */

public class ServletConnectionImpl implements ServletConnection {

	private final ServletVariantClient wrapClient;
	private final Connection bareConnection;
		
	/**
	 * Wrap the bare session, but only if we haven't already. We do this
	 * to preserve the bare API's idempotency of the getSession() calls, i.e.
	 * we don't want to re-wrap the same bare session into distinct wrapper
	 * sessions.
	 * 
	 * @param bareSsn
	 * @return
	 */
	private ServletSessionImpl wrap(final Session bareSsn) {
		
		 return bareSsn == null ? null : new ServletSessionImpl(this, bareSsn);
	}
	
	/**
	 */
	public ServletConnectionImpl(ServletVariantClient wrapClient, Connection bareConnection) {
		this.wrapClient = wrapClient;
		this.bareConnection = bareConnection;		
	}
	
	@Override
	public ServletVariantClient getClient() {
		return wrapClient;
	}

	@Override
	public ServletSession getOrCreateSession(HttpServletRequest req) {
		return wrap(bareConnection.getOrCreateSession(req));
	}

	@Override
	public Optional<ServletSession> getSession(HttpServletRequest req) {
		return bareConnection.getSession(req).map((ssn) -> wrap(ssn));
	}

	@Override
	public Optional<ServletSession> getSessionById(String sid) {
		return bareConnection.getSessionById(sid).map((ssn) -> wrap(ssn));
	}

	@Override
	public String getSchemaName() {
		return bareConnection.getSchemaName();
	}

	@Override
	public ServletSession getOrCreateSession(Object... userData) {
		if (userData.length != 1 || !(userData[0] instanceof HttpServletRequest)) 
			throw new ServletVariantException("User data must have one element of type HttpServletRequest");
		return getOrCreateSession((HttpServletRequest) userData[0]);
	}

	@Override
	public Optional<ServletSession> getSession(Object... userData) {
		if (userData.length != 1 || !(userData[0] instanceof HttpServletRequest)) 
			throw new ServletVariantException("User data must have one element of type HttpServletRequest");
		return getSession((HttpServletRequest) userData[0]);
	}
	
}
