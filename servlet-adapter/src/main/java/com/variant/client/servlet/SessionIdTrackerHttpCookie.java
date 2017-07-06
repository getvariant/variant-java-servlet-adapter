package com.variant.client.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.variant.client.Connection;
import com.variant.client.SessionIdTracker;
import com.variant.client.servlet.util.VariantCookie;

/**
 * Concrete implementation of Variant session ID tracker based on HTTP cookie. 
 * Session ID is saved between state request in a browser's session-scoped cookie.
 * 
 * @see SessionIdTracker
 * @author Igor Urisman
 * @since 1.0
 */
public class SessionIdTrackerHttpCookie implements SessionIdTracker {
			
	/**
	 * Session ID tracking cookie.
	 */
	private static class SsnIdCookie extends VariantCookie {

		SsnIdCookie(String sid) {
			super(COOKIE_NAME);
			super.setValue(sid);
		}
		
		SsnIdCookie(HttpServletRequest request) {
			super(COOKIE_NAME, request);
		}

		/**
		 * Session scoped cookie.
		 */
		@Override
		protected int getMaxAge() {
			return -1;
		}
	}

	private SsnIdCookie cookie = null;

	//---------------------------------------------------------------------------------------------//
	//                                          PUBLIC                                             //
	//---------------------------------------------------------------------------------------------//
	
	public static final String COOKIE_NAME = "variant-ssnid";

	/**
	 * No-argument constructor must be provided by contract.
	 */
	public SessionIdTrackerHttpCookie() {}

	// Since 1.0
	@Override
	public void init(Connection conn, Object...userData) {		
		HttpServletRequest request = (HttpServletRequest) userData[0];
		cookie = new SsnIdCookie(request);
	}
	
	// @since 1.0
	@Override
	public String get() {
		return cookie == null ? null : cookie.getValue();
	}

	// @since 1.0
	@Override
	public void set(String sessionId) {
		cookie.setValue(sessionId);
	}

	// @since 1.0
	@Override
	public void save(Object... userData) {
		HttpServletResponse response = (HttpServletResponse) userData[0];
		cookie.send(response);		
	}
}
