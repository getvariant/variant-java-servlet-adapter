package com.variant.client.servlet.impl;

import java.util.LinkedHashMap;

import javax.servlet.http.HttpServletRequest;

import com.variant.client.ClientException;
import com.variant.client.Connection;
import com.variant.client.Session;
import com.variant.client.VariantClient;
import com.variant.client.lifecycle.ClientLifecycleEvent;
import com.variant.client.lifecycle.LifecycleHook;
import com.variant.client.lifecycle.SessionExpiredLifecycleEvent;
import com.variant.client.SessionExpiredException;
import com.variant.client.servlet.ServletConnection;
import com.variant.client.servlet.ServletSession;
import com.variant.client.servlet.ServletVariantClient;
import com.variant.core.ConnectionStatus;
import com.variant.core.schema.Schema;

/**
 * The implementation of {@link ServletConnection}.
 * 
 * @author Igor Urisman
 * @since 1.0
 */

public class ServletConnectionImpl implements ServletConnection {

	private final ServletVariantClient wrapClient;
	private final Connection bareConnection;
	
	// Keep session wrappers in a map keyed by session ID. Expired sessions remove themselves.
	private final LinkedHashMap<String, ServletSessionImpl> sessionMap = new LinkedHashMap<String, ServletSessionImpl>();
	
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
		
		if (bareSsn == null) return null;
		
		// If this bare session has already been wrapped, don't re-wrap.
		ServletSessionImpl result = sessionMap.get(bareSsn.getId());
		if (result == null) {
			// Not yet been wrapped.  Add the expiration listener to ensure removal from sessionMap.
			result = new ServletSessionImpl(this, bareSsn);
			sessionMap.put(bareSsn.getId(), result);
		}
		return result;
	}
	
	/**
	 */
	public ServletConnectionImpl(ServletVariantClient wrapClient, Connection bareConnection) {
		this.wrapClient = wrapClient;
		this.bareConnection = bareConnection;
		
		// All sessions created by this connection will self-clean on expiration.
		addLifecycleHook(
				new LifecycleHook<SessionExpiredLifecycleEvent>() {
					
					@Override
					public Class<SessionExpiredLifecycleEvent> getLifecycleEventClass() {
						return SessionExpiredLifecycleEvent.class;
					}

					@Override
					public void post(SessionExpiredLifecycleEvent event) throws Exception {
						sessionMap.remove(event.getSession().getId());
					}
				});
	}
	
	@Override
	public String getId() {
		return bareConnection.getId();
	}

	@Override
	public void close() {
		bareConnection.close();
	}

	@Override
	public VariantClient getClient() {
		return wrapClient;
	}

	@Override
	public ServletSession getOrCreateSession(Object... userData) {
		if (userData.length != 1 || !(userData[0] instanceof HttpServletRequest)) 
			throw new ClientException.User("User data must have one element of type HttpServletRequest");
		return getOrCreateSession((HttpServletRequest) userData[0]);
	}

	public ServletSession getOrCreateSession(HttpServletRequest req) {
		return wrap(bareConnection.getOrCreateSession(req));
	}

	@Override
	public Schema getSchema() {
		return bareConnection.getSchema();
	}

	@Override
	public ServletSession getSession(Object... userData) {
		if (userData.length != 1 || !(userData[0] instanceof HttpServletRequest)) 
			throw new ClientException.User("User data must have one element of type HttpServletRequest");
		return getSession((HttpServletRequest) userData[0]);
	}

	public ServletSession getSession(HttpServletRequest req) {
		return wrap(bareConnection.getSession(req));
	}

	@Override
	public ServletSession getSessionById(String sid) {
		return wrap(bareConnection.getSessionById(sid));
	}

	@Override
	public ConnectionStatus getStatus() {
		return bareConnection.getStatus();
	}

	@Override
	public void addLifecycleHook(LifecycleHook<? extends ClientLifecycleEvent> hook) {
		bareConnection.addLifecycleHook(hook);
	}
	
}
