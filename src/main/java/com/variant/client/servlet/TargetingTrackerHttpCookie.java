package com.variant.client.servlet;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.variant.client.TargetingTracker;
import com.variant.client.servlet.util.VariantCookie;
import com.variant.client.impl.TargetingTrackerString;
import com.variant.core.util.TimeUtils;

/**
 * Concrete implementation of the Variant targeting tracker based on HTTP cookie. 
 * Targeting information is saved between sessions in a persistent cookie. 
 * As such, provides "weak" experience stability, i.e. a return user sees the same
 * experiences, so long as he's using the same Web browser and that cookies have not 
 * been removed since the last visit. 
 * 
 * @since 0.5
 */
public class TargetingTrackerHttpCookie extends TargetingTrackerString implements TargetingTracker {
	
	final private static Logger LOG = LoggerFactory.getLogger(TargetingTrackerHttpCookie.class);

	/**
	 * The cookie which tracks the experiences
	 */
	private static class TargetingCookie extends VariantCookie {

		private TargetingCookie() {
			super(COOKIE_NAME);
		}
		
		private TargetingCookie(HttpServletRequest request) {
			super(COOKIE_NAME, request);
		}

		/**
		 * We want this cookie to never expire, so set a year in the future.
		 */
		@Override
		public int getMaxAge() {
			return (int) TimeUtils.SECONDS_PER_DAY * 365;
		}
	}

	private final TargetingCookie cookie;
	
	//---------------------------------------------------------------------------------------------//
	//                                          PUBLIC                                             //
	//---------------------------------------------------------------------------------------------//

	public static final String COOKIE_NAME = "variant-target";

	/**
	 * Constructor must be provided by contract.
	 */
	public TargetingTrackerHttpCookie(Object...userData) {
		HttpServletRequest request =  (HttpServletRequest) userData[0];
		cookie = new TargetingCookie(request);
	}

	// @since 1.0
	@Override
	public Set<Entry> get() {
		String input = cookie.getValue();
		// If the targeting cookie existed and returned a value, the superclass will parse it.
		return input == null ? null : fromString(cookie.getValue());
	}

	// @since 1.0
	@Override
	public void set(Set<Entry> entries) {
		cookie.setValue(toString(entries));
	}

	// @since 1.0
	@Override
	public void save(Object...userData) {
		HttpServletResponse response = (HttpServletResponse) userData[0];
		if (LOG.isDebugEnabled()) {
			LOG.debug("Sending targeting cookie [" + cookie.getValue() + "]");
		}
		cookie.send(response);
	}
}
