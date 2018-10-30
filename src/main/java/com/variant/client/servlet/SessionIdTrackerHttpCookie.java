package com.variant.client.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.variant.client.SessionIdTracker;
import com.variant.client.servlet.util.VariantCookie;

/**
 * Concrete implementation of Variant session ID tracker based on HTTP cookie. 
 * Session ID is saved between state request in a browser's session-scoped cookie.
 * 
 * @see SessionIdTracker
 * @since 0.5
 */
public class SessionIdTrackerHttpCookie implements SessionIdTracker {
			
	final private static Logger LOG = LoggerFactory.getLogger(SessionIdTrackerHttpCookie.class);

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

	@Override
	public void init(Object...userData) {		
		HttpServletRequest request = (HttpServletRequest) userData[0];
		cookie = new SsnIdCookie(request);
	}
	
	@Override
	public String get() {
		return cookie == null ? null : cookie.getValue();
	}

	@Override
	public void set(String sessionId) {
		cookie.setValue(sessionId);
	}

	@Override
	public void save(Object... userData) {
		HttpServletResponse response = (HttpServletResponse) userData[0];
		if (LOG.isDebugEnabled()) {
			LOG.debug("Sending session ID cookie [" + cookie.getValue() + "]");
		}
		cookie.send(response);
	}
}
